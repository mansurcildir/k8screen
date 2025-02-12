package io.k8screen.backend.service;

import io.k8screen.backend.config.ApiClientFactory;
import io.k8screen.backend.data.user.UserItem;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class KubernetesService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public KubernetesService(
      final @NotNull UserService userService, final @NotNull ApiClientFactory apiClientFactory) {
    this.userService = userService;
    this.apiClientFactory = apiClientFactory;
  }

  public List<String> getNodes(final @NotNull String userId) throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1NodeList nodeList = coreV1Api.listNode().execute();
    return nodeList.getItems().stream()
        .map(V1Node::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }
}
