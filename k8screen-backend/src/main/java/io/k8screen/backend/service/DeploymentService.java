package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.DeploymentDTO;
import io.k8screen.backend.mapper.DeploymentConverter;
import io.k8screen.backend.mapper.PodConverter;
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
  private final @NotNull DeploymentConverter deploymentConverter;

  public DeploymentService(final @NotNull AppsV1Api appsV1Api, final @NotNull DeploymentConverter deploymentConverter) {
    this.appsV1Api = appsV1Api;
    this.deploymentConverter = deploymentConverter;
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

  public List<DeploymentDTO> findAll(final @NotNull String namespace) throws Exception {
    final V1DeploymentList deploymentList =
        this.appsV1Api.listNamespacedDeployment(namespace).execute();
    return deploymentList.getItems().stream().map(this.deploymentConverter::toDeploymentDTO).toList();
  }

  public DeploymentDTO findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    V1Deployment deployment = this.appsV1Api.readNamespacedDeployment(name, namespace).execute();
    return this.deploymentConverter.toDeploymentDTO(deployment);
  }

  public V1Status deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.appsV1Api.deleteNamespacedDeployment(name, namespace).execute();
  }
}
