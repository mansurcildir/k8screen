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
public class PodLogHandler extends TextWebSocketHandler {

  private final PodService podService;

  public PodLogHandler(final @NotNull PodService podService) {
    this.podService = podService;
  }

  public void afterConnectionEstablished(final @NotNull WebSocketSession session) {
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
    final String userId = this.decode(params.get("userId").getFirst());

    log.info(
        "Received parameters: namespace={}, podName={}, userId={}", namespace, podName, userId);

    new Thread(
            () -> {
              try {
                this.streamPodLogs(namespace, podName, userId, session);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .start();
  }

  @Override
  public void handleTextMessage(
      final @NotNull WebSocketSession session, final @NotNull TextMessage message) {}

  @Override
  public void afterConnectionClosed(
      final @NotNull WebSocketSession session, final @NotNull CloseStatus status) {}

  private String decode(final @NotNull String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  private void streamPodLogs(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull String userId,
      final @NotNull WebSocketSession session)
      throws Exception {

    this.podService.streamPodLogs(namespace, podName, userId, session);
  }
}
