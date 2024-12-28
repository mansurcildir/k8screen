package io.k8screen.backend.service;

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

  private final @NotNull CoreV1Api coreV1Api;

  public KubernetesService(final @NotNull CoreV1Api coreV1Api) {
    this.coreV1Api = coreV1Api;
  }

  public List<String> getNodes() throws Exception {
    final V1NodeList nodeList = this.coreV1Api.listNode().execute();
    return nodeList.getItems().stream()
        .map(V1Node::getMetadata)
        .filter(Objects::nonNull)
        .map(V1ObjectMeta::getName)
        .toList();
  }
}
