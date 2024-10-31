package io.k8screen.backend.service;

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

  public SecretService(final @NotNull CoreV1Api coreV1Api) {
    this.coreV1Api = coreV1Api;
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

  public V1Secret findByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.readNamespacedSecret(name, namespace).execute();
  }

  public List<V1Secret> findAll(final @NotNull String namespace) throws Exception {
    final V1SecretList secretList = this.coreV1Api.listNamespacedSecret(namespace).execute();
    return secretList.getItems();
  }

  public V1Status deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.deleteNamespacedSecret(name, namespace).execute();
  }
}
