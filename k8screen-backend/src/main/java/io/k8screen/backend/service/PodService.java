package io.k8screen.backend.service;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Streams;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PodService {

  private final @NotNull CoreV1Api coreV1Api;
  private final @NotNull Exec exec;

  public PodService(final @NotNull CoreV1Api coreV1Api, final @NotNull Exec exec) {
    this.coreV1Api = coreV1Api;
    this.exec = exec;
  }

  public List<V1Pod> findAll(final @NotNull String namespace) throws Exception {
    final V1PodList podList = this.coreV1Api.listNamespacedPod(namespace).execute();
    return podList.getItems();
  }

  public V1Pod findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.readNamespacedPod(name, namespace).execute();
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
