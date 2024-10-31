package io.k8screen.backend.service;

import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class DeploymentService {

  private final @NotNull AppsV1Api appsV1Api;

  public DeploymentService(final @NotNull AppsV1Api appsV1Api) {
    this.appsV1Api = appsV1Api;
  }

  public V1Deployment create(
      final @NotNull String namespace, final @NotNull V1Deployment deployment) throws Exception {
    return this.appsV1Api.createNamespacedDeployment(namespace, deployment).execute();
  }

  public V1Deployment updateByName(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Deployment deployment)
      throws Exception {
    return this.appsV1Api.replaceNamespacedDeployment(name, namespace, deployment).execute();
  }

  public List<V1Deployment> findAll(final @NotNull String namespace) throws Exception {
    final V1DeploymentList deploymentList =
        this.appsV1Api.listNamespacedDeployment(namespace).execute();
    return deploymentList.getItems();
  }

  public V1Deployment findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.appsV1Api.readNamespacedDeployment(name, namespace).execute();
  }

  public V1Status deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.appsV1Api.deleteNamespacedDeployment(name, namespace).execute();
  }
}
