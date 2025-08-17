package io.k8screen.backend.k8s.secret;

import io.k8screen.backend.k8s.secret.dto.SecretInfo;
import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
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
public class SecretService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull SecretConverter secretConverter;

  public @NotNull V1Secret create(
      final @NotNull String namespace, final @NotNull V1Secret secret, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    return coreV1Api.createNamespacedSecret(namespace, secret).execute();
  }

  public @NotNull V1Secret update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Secret secret,
      final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    return coreV1Api.replaceNamespacedSecret(name, namespace, secret).execute();
  }

  public @NotNull SecretInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1Secret secret = coreV1Api.readNamespacedSecret(name, namespace).execute();
    return this.secretConverter.toSecretInfo(secret);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1Secret secret = coreV1Api.readNamespacedSecret(name, namespace).execute();
    if (secret.getMetadata() != null && secret.getMetadata().getManagedFields() != null) {
      secret.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(secret);
  }

  public @NotNull List<SecretInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1SecretList secretList = coreV1Api.listNamespacedSecret(namespace).execute();
    return secretList.getItems().stream().map(this.secretConverter::toSecretInfo).toList();
  }

  public @NotNull V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    return coreV1Api.deleteNamespacedSecret(name, namespace).execute();
  }
}
