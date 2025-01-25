package io.k8screen.backend.service;

import io.k8screen.backend.util.ApiClientFactory;
import io.k8screen.backend.data.dto.PodDTO;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.PodConverter;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Streams;
import io.kubernetes.client.util.Yaml;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PodService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull PodConverter podConverter;

  public List<PodDTO> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1PodList podList = coreV1Api.listNamespacedPod(namespace).execute();
    return podList.getItems().stream().map(this.podConverter::toPodDTO).toList();
  }

  public PodDTO findByName(
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

  public String findLogs(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.readNamespacedPodLog(name, namespace).execute();
  }

  public String exec(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull String[] command,
      final @NotNull String userId)
      throws IOException, InterruptedException, ApiException {

    final UserItem user = this.userService.findById(userId);
    final Exec exec = this.apiClientFactory.exec(user.config(), userId);

    final Process proc = exec.exec(namespace, name, command, true, true);
    final StringBuilder outputBuilder = new StringBuilder();

    final Thread inputThread =
        new Thread(
            () -> {
              try {
                Streams.copy(System.in, proc.getOutputStream());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

    final Thread outputThread =
        new Thread(
            () -> {
              try {
                Streams.copy(
                    proc.getInputStream(),
                    new OutputStream() {
                      @Override
                      public void write(final int b) throws IOException {
                        outputBuilder.append((char) b);
                      }
                    });
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

    inputThread.start();
    outputThread.start();
    proc.waitFor();
    outputThread.join();
    proc.destroy();
    return outputBuilder.toString();
  }
}
