package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.PodDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PodConverter {
  public PodDTO toPodDTO(final @NotNull V1Pod pod) {
    final String name = Objects.requireNonNull(pod.getMetadata()).getName();

    final List<V1ContainerStatus> containerStatuses =
        Objects.requireNonNull(pod.getStatus()).getContainerStatuses();
    final int totalContainers = containerStatuses != null ? containerStatuses.size() : 0;
    final int readyContainers =
        containerStatuses != null
            ? (int) containerStatuses.stream().filter(V1ContainerStatus::getReady).count()
            : 0;
    final String status = pod.getStatus().getPhase();

    final int restarts =
        containerStatuses != null
            ? containerStatuses.stream().mapToInt(V1ContainerStatus::getRestartCount).sum()
            : 0;

    final OffsetDateTime creationTimestamp = pod.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatAge(creationTimestamp);
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
