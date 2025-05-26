package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.k8s.StatefulSetInfo;
import io.k8screen.backend.util.FormatUtil;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StatefulSetConverter {
  public @NotNull StatefulSetInfo toStatefulSetDTO(final @NotNull V1StatefulSet statefulSet) {
    final var metaData = Objects.requireNonNull(statefulSet.getMetadata());
    final var status = Objects.requireNonNull(statefulSet.getStatus());
    final var spec = Objects.requireNonNull(statefulSet.getSpec());

    final String name = Objects.requireNonNull(metaData.getName());
    final int totalReplicas = Objects.requireNonNull(spec.getReplicas());
    final int readyReplicas = Objects.requireNonNull(status.getReadyReplicas());

    final OffsetDateTime creationTimestamp =
        Objects.requireNonNull(metaData.getCreationTimestamp());
    final String age = FormatUtil.formatAge(creationTimestamp);

    return StatefulSetInfo.builder()
        .name(name)
        .totalReplicas(totalReplicas)
        .readyReplicas(readyReplicas)
        .age(age)
        .build();
  }
}
