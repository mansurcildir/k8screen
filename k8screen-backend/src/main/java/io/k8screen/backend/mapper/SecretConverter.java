package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.SecretDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Secret;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SecretConverter {
  public SecretDTO toSecretDTO(final @NotNull V1Secret secret) {

    final String name = secret.getMetadata().getName();
    final String type = secret.getType();
    final int dataSize = secret.getData().size();

    final OffsetDateTime creationTimestamp = secret.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return SecretDTO.builder().name(name).type(type).dataSize(dataSize).age(age).build();
  }
}
