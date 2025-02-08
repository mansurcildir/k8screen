package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.ServiceInfo;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ServiceConverter {

  public ServiceInfo toServiceDTO(final @NotNull V1Service service) {
    final String serviceName = Objects.requireNonNull(service.getMetadata()).getName();
    final String serviceType = Objects.requireNonNull(service.getSpec()).getType();
    final String clusterIp = service.getSpec().getClusterIP();
    final String externalIp =
        (service.getSpec().getExternalIPs() != null
                && !service.getSpec().getExternalIPs().isEmpty())
            ? service.getSpec().getExternalIPs().getFirst()
            : "none";

    final List<String> ports =
        Objects.requireNonNull(service.getSpec().getPorts()).stream()
            .map(port -> port.getPort().toString())
            .toList();

    final OffsetDateTime creationTimestamp = service.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatAge(creationTimestamp);
    }

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
