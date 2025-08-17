package io.k8screen.backend.k8s.deployment;

import io.k8screen.backend.k8s.deployment.dto.DeploymentInfo;
import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeploymentService {

  private final @NotNull DeploymentConverter deploymentConverter;
  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public @NotNull V1Deployment create(
      final @NotNull String namespace,
      final @NotNull V1Deployment deployment,
      final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.createNamespacedDeployment(namespace, deployment).execute();
  }

  public @NotNull V1Deployment updateByName(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Deployment deployment,
      final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.replaceNamespacedDeployment(name, namespace, deployment).execute();
  }

  public @NotNull List<DeploymentInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    final V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();
    return deploymentList.getItems().stream()
        .map(this.deploymentConverter::toDeploymentInfo)
        .toList();
  }

  public @NotNull DeploymentInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    final V1Deployment deployment = appsV1Api.readNamespacedDeployment(name, namespace).execute();
    return this.deploymentConverter.toDeploymentInfo(deployment);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    final V1Deployment deployment = appsV1Api.readNamespacedDeployment(name, namespace).execute();
    if (deployment.getMetadata() != null && deployment.getMetadata().getManagedFields() != null) {
      deployment.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(deployment);
  }

  public @NotNull V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.deleteNamespacedDeployment(name, namespace).execute();
  }
}
