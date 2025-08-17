package io.k8screen.backend.k8s.node;

import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
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
public class NodeService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;

  public @NotNull List<String> getNodes(final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(userInfo.activeConfig(), userUuid);
    final V1NodeList nodeList = coreV1Api.listNode().execute();
    return nodeList.getItems().stream()
        .map(V1Node::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }
}
