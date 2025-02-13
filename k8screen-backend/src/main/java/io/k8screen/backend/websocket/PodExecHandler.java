package io.k8screen.backend.websocket;

import io.k8screen.backend.service.PodService;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  private final PodService podService;

  public PodExecHandler(final @NotNull PodService podService) {
    this.podService = podService;
  }

  @Override
  public void afterConnectionEstablished(final @NotNull WebSocketSession session) {}

  @Override
  public void handleTextMessage(
      final @NotNull WebSocketSession session, final @NotNull TextMessage message) {

    // Parse WebSocket query parameters
    final URI uri = Objects.requireNonNull(session.getUri());
    final String query = uri.getQuery();

    final Map<String, List<String>> params =
        Arrays.stream(query.split("&"))
            .map(param -> param.split("="))
            .collect(
                Collectors.groupingBy(
                    param -> param[0], Collectors.mapping(param -> param[1], Collectors.toList())));

    final String namespace = this.decode(params.get("namespace").get(0));
    final String podName = this.decode(params.get("podName").get(0));
    final String userId = this.decode(params.get("userId").get(0));

    log.info(
        "Received parameters for exec: namespace={}, podName={}, userId={}",
        namespace,
        podName,
        userId);

    final String command = message.getPayload();
    log.info("Received command: {}", command);

    new Thread(() -> this.terminalExec(namespace, podName, userId, session, command)).start();
  }

  @Override
  public void afterConnectionClosed(
      final @NotNull WebSocketSession session, final @NotNull CloseStatus status) {
    log.info("Connection closed: sessionId={}, status={}", session.getId(), status);
  }

  private String decode(final @NotNull String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  private void terminalExec(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull String userId,
      final @NotNull WebSocketSession session,
      final @NotNull String command) {
    try {
      this.podService.terminalExec(namespace, podName, userId, session, command);
    } catch (Exception e) {
      log.error(
          "Error while streaming logs with namespace: {}, podName: {}, userId: {}",
          namespace,
          podName,
          userId);
    }
  }
}
