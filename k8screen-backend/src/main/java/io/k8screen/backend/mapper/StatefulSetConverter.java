package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.StatefulSetDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class StatefulSetConverter {
  public StatefulSetDTO toStatefulSetDTO(V1StatefulSet statefulSet) {
    String name = statefulSet.getMetadata().getName();

    int totalReplicas = statefulSet.getSpec().getReplicas() != null ? statefulSet.getSpec().getReplicas() : 0;

    int readyReplicas = statefulSet.getStatus().getReadyReplicas() != null
      ? statefulSet.getStatus().getReadyReplicas()
      : 0;

    OffsetDateTime creationTimestamp = statefulSet.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return StatefulSetDTO.builder()
      .name(name)
      .totalReplicas(totalReplicas)
      .readyReplicas(readyReplicas)
      .age(age)
      .build();
  }
}
