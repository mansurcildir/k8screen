package io.k8screen.backend.k8s.namespace;

import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NamespaceService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public @NotNull List<String> getAllNamespaces(final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    if (userInfo.activeConfig() == null) {
      return List.of();
    }

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1NamespaceList namespaceList = coreV1Api.listNamespace().execute();
    return namespaceList.getItems().stream()
        .map(V1Namespace::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }

  public @NotNull V1Namespace createNamespace(
      final @NotNull String name, final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1Namespace namespace = new V1Namespace();
    namespace.setApiVersion("v1");
    namespace.setKind("Namespace");
    namespace.setMetadata(new V1ObjectMeta().name(name));

    return coreV1Api.createNamespace(namespace).execute();
  }

  public @NotNull V1Status deleteNamespace(final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    return coreV1Api.deleteNamespace(name).execute();
  }
}
