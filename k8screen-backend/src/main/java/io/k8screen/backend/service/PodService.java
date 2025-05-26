package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.k8s.PodInfo;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.mapper.PodConverter;
import io.k8screen.backend.repository.UserRepository;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Streams;
import io.kubernetes.client.util.Yaml;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PodService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull PodConverter podConverter;

  public @NotNull List<PodInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1PodList podList = coreV1Api.listNamespacedPod(namespace).execute();
    return podList.getItems().stream().map(this.podConverter::toPodInfo).toList();
  }

  public @NotNull PodInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1Pod pod = coreV1Api.readNamespacedPod(name, namespace).execute();
    return this.podConverter.toPodInfo(pod);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1Pod pod = coreV1Api.readNamespacedPod(name, namespace).execute();
    if (pod.getMetadata() != null && pod.getMetadata().getManagedFields() != null) {
      pod.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(pod);
  }

  public @NotNull V1Pod deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    return coreV1Api.deleteNamespacedPod(name, namespace).execute();
  }

  public void streamPodLogs(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull UUID userUuid,
      final @NotNull WebSocketSession session)
      throws Exception {

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);

    new Thread(() -> this.fetchLogs(coreV1Api, namespace, podName, session)).start();
  }

  public void terminalExec(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull UUID userUuid,
      final @NotNull WebSocketSession session,
      final @NotNull String command)
      throws Exception {

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Exec exec = this.apiClientFactory.exec(user.getActiveConfig(), userUuid);

    final String[] commandArray = {"sh", "-c", command};
    final Process proc = exec.exec(namespace, podName, commandArray, null, true, true);
    final StringBuilder stringBuilder = new StringBuilder();

    final Thread thread = new Thread(() -> this.execInputStream(proc, stringBuilder));

    thread.start();
    proc.waitFor();
    thread.join();
    proc.destroy();
    session.sendMessage(new TextMessage(stringBuilder.toString()));
  }

  private void execInputStream(final @NotNull Process proc, final StringBuilder stringBuilder) {
    try {
      Streams.copy(
          proc.getInputStream(),
          new OutputStream() {
            @Override
            public void write(final int b) {
              stringBuilder.append((char) b);
            }
          });
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void fetchLogs(
      final @NotNull CoreV1Api coreV1Api,
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull WebSocketSession session) {
    try {
      while (true) {
        final String logs = coreV1Api.readNamespacedPodLog(podName, namespace).execute();
        try {
          if (session.isOpen()) {
            session.sendMessage(new TextMessage(logs));
          }
        } catch (IOException e) {
          log.error("Error sending log to WebSocket: {}", e.getMessage());
        }
        Thread.sleep(1000);
      }
    } catch (Exception e) {
      log.error("Error while streaming logs: {}", e.getMessage());
    }
  }
}
