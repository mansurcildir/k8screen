package io.k8screen.backend.service;

import io.k8screen.backend.util.ApiClientFactory;
import io.k8screen.backend.data.dto.DeploymentDTO;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.DeploymentConverter;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DeploymentService {

  private final @NotNull DeploymentConverter deploymentConverter;
  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public V1Deployment create(
      final @NotNull String namespace,
      final @NotNull V1Deployment deployment,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.createNamespacedDeployment(namespace, deployment).execute();
  }

  public V1Deployment updateByName(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Deployment deployment,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.replaceNamespacedDeployment(name, namespace, deployment).execute();
  }

  public List<DeploymentDTO> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();
    return deploymentList.getItems().stream()
        .map(this.deploymentConverter::toDeploymentDTO)
        .toList();
  }

  public DeploymentDTO findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1Deployment deployment = appsV1Api.readNamespacedDeployment(name, namespace).execute();
    return this.deploymentConverter.toDeploymentDTO(deployment);
  }

  public String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1Deployment deployment = appsV1Api.readNamespacedDeployment(name, namespace).execute();
    if (deployment.getMetadata() != null && deployment.getMetadata().getManagedFields() != null) {
      deployment.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(deployment);
  }

  public V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.deleteNamespacedDeployment(name, namespace).execute();
  }
}
