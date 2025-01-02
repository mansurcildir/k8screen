package io.k8screen.backend.service;

import io.k8screen.backend.config.ApiClientFactory;
import io.k8screen.backend.data.dto.SecretDTO;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.SecretConverter;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SecretService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull SecretConverter secretConverter;

  public SecretService(
      final @NotNull UserService userService,
      final @NotNull ApiClientFactory apiClientFactory,
      final @NotNull SecretConverter secretConverter) {
    this.userService = userService;
    this.apiClientFactory = apiClientFactory;
    this.secretConverter = secretConverter;
  }

  public V1Secret create(
      final @NotNull String namespace, final @NotNull V1Secret secret, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.createNamespacedSecret(namespace, secret).execute();
  }

  public V1Secret updateByName(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Secret secret,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.replaceNamespacedSecret(name, namespace, secret).execute();
  }

  public SecretDTO findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Secret secret = coreV1Api.readNamespacedSecret(name, namespace).execute();
    return this.secretConverter.toSecretDTO(secret);
  }

  public String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Secret secret = coreV1Api.readNamespacedSecret(name, namespace).execute();
    if (secret.getMetadata() != null && secret.getMetadata().getManagedFields() != null) {
      secret.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(secret);
  }

  public List<SecretDTO> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1SecretList secretList = coreV1Api.listNamespacedSecret(namespace).execute();
    return secretList.getItems().stream().map(this.secretConverter::toSecretDTO).toList();
  }

  public V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.deleteNamespacedSecret(name, namespace).execute();
  }
}
