package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.StatefulSetDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StatefulSetConverter {
  public StatefulSetDTO toStatefulSetDTO(final @NotNull V1StatefulSet statefulSet) {
    final String name = statefulSet.getMetadata().getName();

    final int totalReplicas =
        statefulSet.getSpec().getReplicas() != null ? statefulSet.getSpec().getReplicas() : 0;

    final int readyReplicas =
        statefulSet.getStatus().getReadyReplicas() != null
            ? statefulSet.getStatus().getReadyReplicas()
            : 0;

    final OffsetDateTime creationTimestamp = statefulSet.getMetadata().getCreationTimestamp();
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
