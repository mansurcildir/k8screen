package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.SecretDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Secret;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SecretConverter {
  public SecretDTO toSecretDTO(V1Secret secret) {

    String name = secret.getMetadata().getName();
    String type = secret.getType();
    int dataSize = secret.getData().size();

    OffsetDateTime creationTimestamp = secret.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return SecretDTO.builder()
      .name(name)
      .type(type)
      .dataSize(dataSize)
      .age(age)
      .build();
  }
}
