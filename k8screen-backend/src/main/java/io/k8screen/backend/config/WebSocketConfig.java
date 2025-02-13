package io.k8screen.backend.config;

import io.k8screen.backend.websocket.PodExecHandler;
import io.k8screen.backend.websocket.PodLogHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final @NotNull PodLogHandler podLogHandler;
  private final @NotNull PodExecHandler podExecHandler;

  public WebSocketConfig(
      final @NotNull PodLogHandler podLogHandler, final @NotNull PodExecHandler podExecHandler) {
    this.podLogHandler = podLogHandler;
    this.podExecHandler = podExecHandler;
  }

  @Override
  public void registerWebSocketHandlers(final @NotNull WebSocketHandlerRegistry registry) {
    registry.addHandler(this.podLogHandler, "/ws/logs").setAllowedOrigins("*");
    registry.addHandler(this.podExecHandler, "/ws/exec").setAllowedOrigins("*");
  }
}
