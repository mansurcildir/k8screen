package io.k8screen.backend.k8s.service;

import io.k8screen.backend.k8s.service.dto.ServiceInfo;
import io.k8screen.backend.util.FormatUtil;
import io.kubernetes.client.openapi.models.V1Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ServiceConverter {
  public @NotNull ServiceInfo toServiceInfo(final @NotNull V1Service service) {
    final var metaData = Objects.requireNonNull(service.getMetadata());
    final var spec = Objects.requireNonNull(service.getSpec());

    final String serviceName = Objects.requireNonNull(metaData.getName());
    final String serviceType = Objects.requireNonNull(spec.getType());
    final String clusterIp = Objects.requireNonNull(spec.getClusterIP());
    final String externalIp =
        Optional.ofNullable(spec.getExternalIPs())
            .filter(list -> !list.isEmpty())
            .map(List::getFirst)
            .orElse("none");

    final List<String> ports =
        Objects.requireNonNull(service.getSpec().getPorts()).stream()
            .map(port -> port.getPort().toString())
            .toList();

    final OffsetDateTime creationTimestamp =
        Objects.requireNonNull(metaData.getCreationTimestamp());
    final String age = FormatUtil.formatAge(creationTimestamp);

    return ServiceInfo.builder()
        .name(serviceName)
        .type(serviceType)
        .clusterIp(clusterIp)
        .externalIp(externalIp)
        .ports(ports)
        .age(age)
        .build();
  }
}
