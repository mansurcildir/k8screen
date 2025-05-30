package io.k8screen.backend.service;

import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.repository.UserRepository;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class KubernetesService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ApiClientFactory apiClientFactory;

  public @NotNull List<String> getNodes(final @NotNull UUID userUuid) throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1NodeList nodeList = coreV1Api.listNode().execute();
    return nodeList.getItems().stream()
        .map(V1Node::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }
}
