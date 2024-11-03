package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.SecretDTO;
import io.k8screen.backend.mapper.SecretConverter;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SecretService {

  private final @NotNull CoreV1Api coreV1Api;
  private final @NotNull SecretConverter secretConverter;

  public SecretService(final @NotNull CoreV1Api coreV1Api, final @NotNull SecretConverter secretConverter) {
    this.coreV1Api = coreV1Api;
    this.secretConverter = secretConverter;
  }

  public V1Secret create(final @NotNull String namespace, final @NotNull V1Secret secret)
      throws Exception {
    return this.coreV1Api.createNamespacedSecret(namespace, secret).execute();
  }

  public V1Secret updateByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull V1Secret secret)
      throws Exception {
    return this.coreV1Api.replaceNamespacedSecret(name, namespace, secret).execute();
  }

  public SecretDTO findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    V1Secret secret = this.coreV1Api.readNamespacedSecret(name, namespace).execute();
    return secretConverter.toSecretDTO(secret);
  }

  public List<SecretDTO> findAll(final @NotNull String namespace) throws Exception {
    final V1SecretList secretList = this.coreV1Api.listNamespacedSecret(namespace).execute();
    return secretList.getItems().stream().map(this.secretConverter::toSecretDTO).toList();
  }

  public V1Status deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.deleteNamespacedSecret(name, namespace).execute();
  }
}
