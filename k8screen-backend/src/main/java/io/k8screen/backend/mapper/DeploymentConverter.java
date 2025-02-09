package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.k8s.DeploymentInfo;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Deployment;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DeploymentConverter {
  public DeploymentInfo toDeploymentDTO(final @NotNull V1Deployment deployment) {
    final String name = Objects.requireNonNull(deployment.getMetadata()).getName();

    final int readyReplicas =
        Objects.requireNonNull(deployment.getStatus()).getReadyReplicas() != null
            ? deployment.getStatus().getReadyReplicas()
            : 0;
    final int totalReplicas =
        Objects.requireNonNull(deployment.getSpec()).getReplicas() != null
            ? deployment.getSpec().getReplicas()
            : 0;

    final int upToDate =
        deployment.getStatus().getUpdatedReplicas() != null
            ? deployment.getStatus().getUpdatedReplicas()
            : 0;

    final int available =
        deployment.getStatus().getAvailableReplicas() != null
            ? deployment.getStatus().getAvailableReplicas()
            : 0;

    final OffsetDateTime creationTimestamp = deployment.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatAge(creationTimestamp);
    }

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
