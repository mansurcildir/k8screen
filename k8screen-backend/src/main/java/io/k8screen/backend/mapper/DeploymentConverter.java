package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.DeploymentDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DeploymentConverter {
  public DeploymentDTO toDeploymentDTO(V1Deployment deployment) {
    String name = deployment.getMetadata().getName();

    int readyReplicas = deployment.getStatus().getReadyReplicas() != null ? deployment.getStatus().getReadyReplicas() : 0;
    int totalReplicas = deployment.getSpec().getReplicas() != null ? deployment.getSpec().getReplicas() : 0;

    int upToDate = deployment.getStatus().getUpdatedReplicas() != null
      ? deployment.getStatus().getUpdatedReplicas()
      : 0;

    int available = deployment.getStatus().getAvailableReplicas() != null
      ? deployment.getStatus().getAvailableReplicas()
      : 0;

    OffsetDateTime creationTimestamp = deployment.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return DeploymentDTO.builder()
      .name(name)
      .totalReplicas(totalReplicas)
      .readyReplicas(readyReplicas)
      .upToDate(upToDate)
      .available(available)
      .age(age)
      .build();
  }

}
