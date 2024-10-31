package io.k8screen.backend.service;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class NamespaceService {
  private final @NotNull CoreV1Api coreV1Api;

  public NamespaceService(final @NotNull CoreV1Api coreV1Api) {
    this.coreV1Api = coreV1Api;
  }

  public List<String> getAllNamespaces() throws Exception {
    final V1NamespaceList namespaceList = this.coreV1Api.listNamespace().execute();
    return namespaceList.getItems().stream()
        .map(V1Namespace::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }

  public V1Namespace createNamespace(final @NotNull String name) throws Exception {
    final V1Namespace namespace = new V1Namespace();
    namespace.setApiVersion("v1");
    namespace.setKind("Namespace");
    namespace.setMetadata(new V1ObjectMeta().name(name));

    return this.coreV1Api.createNamespace(namespace).execute();
  }

  public V1Status deleteNamespace(final @NotNull String name) throws Exception {
    return this.coreV1Api.deleteNamespace(name).execute();
  }
}
