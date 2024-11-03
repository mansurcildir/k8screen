package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.PodDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class PodConverter {
  public PodDTO toPodDTO(V1Pod pod) {
    String name = pod.getMetadata().getName();

    List<V1ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
    int totalContainers = containerStatuses != null ? containerStatuses.size() : 0;
    int readyContainers = containerStatuses != null ? (int) containerStatuses.stream()
      .filter(containerStatus -> containerStatus.getReady() != null && containerStatus.getReady())
      .count() : 0;
    String status = pod.getStatus().getPhase();

    int restarts = containerStatuses != null
      ? containerStatuses.stream()
      .mapToInt(containerStatus -> containerStatus.getRestartCount())
      .sum()
      : 0;

    OffsetDateTime creationTimestamp = pod.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return PodDTO.builder()
      .name(name)
      .totalContainers(totalContainers)
      .readyContainers(readyContainers)
      .status(status)
      .restarts(restarts)
      .age(age)
      .build();
  }
}
