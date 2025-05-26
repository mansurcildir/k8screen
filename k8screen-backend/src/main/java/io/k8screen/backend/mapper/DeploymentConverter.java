package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.k8s.DeploymentInfo;
import io.k8screen.backend.util.FormatUtil;
import io.kubernetes.client.openapi.models.V1Deployment;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DeploymentConverter {
  public @NotNull DeploymentInfo toDeploymentInfo(final @NotNull V1Deployment deployment) {
    final var metaData = Objects.requireNonNull(deployment.getMetadata());
    final var status = Objects.requireNonNull(deployment.getStatus());
    final var spec = Objects.requireNonNull(deployment.getSpec());

    final String name = Objects.requireNonNull(metaData.getName());
    final int readyReplicas = Optional.ofNullable(status.getReadyReplicas()).orElse(0);
    final int totalReplicas = Optional.ofNullable(spec.getReplicas()).orElse(0);
    final int upToDate = Optional.ofNullable(status.getUpdatedReplicas()).orElse(0);
    final int available = Optional.ofNullable(status.getAvailableReplicas()).orElse(0);

    final OffsetDateTime creationTimestamp =
        Objects.requireNonNull(metaData.getCreationTimestamp());
    final String age = FormatUtil.formatAge(creationTimestamp);

    return DeploymentInfo.builder()
        .name(name)
        .totalReplicas(totalReplicas)
        .readyReplicas(readyReplicas)
        .upToDate(upToDate)
        .available(available)
        .age(age)
        .build();
  }
}
