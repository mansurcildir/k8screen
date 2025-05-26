package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.k8s.PodInfo;
import io.k8screen.backend.util.FormatUtil;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PodConverter {
  public @NotNull PodInfo toPodInfo(final @NotNull V1Pod pod) {
    final var metaData = Objects.requireNonNull(pod.getMetadata());
    final var status = Objects.requireNonNull(pod.getStatus());
    final var containerStatuses = Objects.requireNonNull(status.getContainerStatuses());

    final String name = Objects.requireNonNull(metaData.getName());
    final int totalContainers = containerStatuses.size();
    final int readyContainers =
        (int) containerStatuses.stream().filter(V1ContainerStatus::getReady).count();

    final String podStatus = Objects.requireNonNull(status.getPhase());
    final int restarts =
        containerStatuses.stream().mapToInt(V1ContainerStatus::getRestartCount).sum();

    final OffsetDateTime creationTimestamp =
        Objects.requireNonNull(metaData.getCreationTimestamp());
    final String age = FormatUtil.formatAge(creationTimestamp);

    return PodInfo.builder()
        .name(name)
        .totalContainers(totalContainers)
        .readyContainers(readyContainers)
        .status(podStatus)
        .restarts(restarts)
        .age(age)
        .build();
  }
}
