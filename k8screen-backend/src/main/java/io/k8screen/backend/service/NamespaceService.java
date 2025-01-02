package io.k8screen.backend.service;

import io.k8screen.backend.config.ApiClientFactory;
import io.k8screen.backend.data.user.UserItem;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NamespaceService {

  @Value("${k8screen.config.path}")
  private String configPath;

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public NamespaceService(
      final @NotNull UserService userService, final @NotNull ApiClientFactory apiClientFactory) {
    this.userService = userService;
    this.apiClientFactory = apiClientFactory;
  }

  public List<String> getAllNamespaces(final @NotNull String userId) throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);

    final V1NamespaceList namespaceList = coreV1Api.listNamespace().execute();
    return namespaceList.getItems().stream()
        .map(V1Namespace::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }

  public V1Namespace createNamespace(final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);

    final V1Namespace namespace = new V1Namespace();
    namespace.setApiVersion("v1");
    namespace.setKind("Namespace");
    namespace.setMetadata(new V1ObjectMeta().name(name));

    return coreV1Api.createNamespace(namespace).execute();
  }

  public V1Status deleteNamespace(final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);

    return coreV1Api.deleteNamespace(name).execute();
  }
}
