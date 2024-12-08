package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.PodDTO;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PodService {

  private final @NotNull CoreV1Api coreV1Api;
  private final @NotNull Exec exec;
  private final @NotNull PodConverter podConverter;

  public PodService(
      final @NotNull CoreV1Api coreV1Api,
      final @NotNull Exec exec,
      final @NotNull PodConverter podConverter) {
    this.coreV1Api = coreV1Api;
    this.exec = exec;
    this.podConverter = podConverter;
  }

  public List<PodDTO> findAll(final @NotNull String namespace) throws Exception {
    final V1PodList podList = this.coreV1Api.listNamespacedPod(namespace).execute();
    return podList.getItems().stream().map(this.podConverter::toPodDTO).toList();
  }

  public PodDTO findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    final V1Pod pod = this.coreV1Api.readNamespacedPod(name, namespace).execute();
    return this.podConverter.toPodDTO(pod);
  }

  public String getDetailByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    final V1Pod pod = this.coreV1Api.readNamespacedPod(name, namespace).execute();
    if (pod.getMetadata() != null && pod.getMetadata().getManagedFields() != null) {
      pod.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(pod);
  }

  public V1Pod deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.deleteNamespacedPod(name, namespace).execute();
  }

  public String findLogs(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.readNamespacedPodLog(name, namespace).execute();
  }

  public String exec(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String[] command)
      throws IOException, InterruptedException, ApiException {

    final Process proc = this.exec.exec(namespace, name, command, true, true);
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
