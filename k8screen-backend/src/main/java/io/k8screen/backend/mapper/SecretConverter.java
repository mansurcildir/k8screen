package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.k8s.SecretInfo;
import io.k8screen.backend.util.FormatUtil;
import io.kubernetes.client.openapi.models.V1Secret;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SecretConverter {
  public @NotNull SecretInfo toSecretInfo(final @NotNull V1Secret secret) {
    final var metaData = Objects.requireNonNull(secret.getMetadata());

    final String name = Objects.requireNonNull(metaData.getName());
    final String type = Objects.requireNonNull(secret.getType());
    final int dataSize = Objects.requireNonNull(secret.getData()).size();

    final OffsetDateTime creationTimestamp =
        Objects.requireNonNull(metaData.getCreationTimestamp());
    final String age = FormatUtil.formatAge(creationTimestamp);

    return SecretInfo.builder().name(name).type(type).dataSize(dataSize).age(age).build();
  }
}
