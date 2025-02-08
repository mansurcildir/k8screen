package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.SecretInfo;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Secret;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SecretConverter {
  public SecretInfo toSecretDTO(final @NotNull V1Secret secret) {

    final String name = Objects.requireNonNull(secret.getMetadata()).getName();
    final String type = secret.getType();
    final int dataSize = Objects.requireNonNull(secret.getData()).size();

    final OffsetDateTime creationTimestamp = secret.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatAge(creationTimestamp);
    }

    return SecretInfo.builder().name(name).type(type).dataSize(dataSize).age(age).build();
  }
}
