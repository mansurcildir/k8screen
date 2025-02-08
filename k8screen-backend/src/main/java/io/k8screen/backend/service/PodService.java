package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.PodInfo;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.PodConverter;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Yaml;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
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

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull PodConverter podConverter;

  public List<PodInfo> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1PodList podList = coreV1Api.listNamespacedPod(namespace).execute();
    return podList.getItems().stream().map(this.podConverter::toPodDTO).toList();
  }

  public PodInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Pod pod = coreV1Api.readNamespacedPod(name, namespace).execute();
    return this.podConverter.toPodDTO(pod);
  }

  public String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Pod pod = coreV1Api.readNamespacedPod(name, namespace).execute();
    if (pod.getMetadata() != null && pod.getMetadata().getManagedFields() != null) {
      pod.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(pod);
  }

  public V1Pod deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.deleteNamespacedPod(name, namespace).execute();
  }

  public void streamPodLogs(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull String userId,
      final @NotNull WebSocketSession session)
      throws Exception {

    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);

    new Thread(
            () -> {
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
            })
        .start();
  }

  public void terminalExec(
      final @NotNull String namespace,
      final @NotNull String podName,
      final @NotNull String userId,
      final @NotNull WebSocketSession session,
      final @NotNull String command)
      throws Exception {

    final UserItem user = this.userService.findById(userId);
    final Exec exec = this.apiClientFactory.exec(user.config(), userId);
  }
}
