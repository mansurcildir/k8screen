package io.k8screen.backend.websocket;

import io.k8screen.backend.service.PodService;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class PodExecHandler extends TextWebSocketHandler {

  private final @NotNull PodService podService;

  public PodExecHandler(final @NotNull PodService podService) {
    this.podService = podService;
  }

  @Override
  public void afterConnectionEstablished(final @NotNull WebSocketSession session) {}

  @Override
  public void handleTextMessage(
      final @NotNull WebSocketSession session, final @NotNull TextMessage message)
      throws Exception {

    // Parse WebSocket query parameters
    final URI uri = Objects.requireNonNull(session.getUri());
    final String query = uri.getQuery();

    final Map<String, List<String>> params =
        Arrays.stream(query.split("&"))
            .map(param -> param.split("="))
            .collect(
                Collectors.groupingBy(
                    param -> param[0], Collectors.mapping(param -> param[1], Collectors.toList())));

    final String namespace = this.decode(params.get("namespace").getFirst());
    final String podName = this.decode(params.get("podName").getFirst());
    final UUID userUuid = UUID.fromString(this.decode(params.get("userUuid").getFirst()));

    log.info(
        "Received parameters for exec: namespace={}, podName={}, userUuid={}",
        namespace,
        podName,
        userUuid);

    final String command = message.getPayload();
    this.podService.terminalExec(namespace, podName, userUuid, session, command);
  }

  @Override
  public void afterConnectionClosed(
      final @NotNull WebSocketSession session, final @NotNull CloseStatus status) {
    log.info("Connection closed: sessionId={}, status={}", session.getId(), status);
  }

  private @NotNull String decode(final @NotNull String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }
}
