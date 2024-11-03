package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.ServiceDTO;
import io.k8screen.backend.util.Util;
import io.kubernetes.client.openapi.models.V1Service;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ServiceConverter {

  public ServiceDTO toServiceDTO(V1Service service) {
    String serviceName = service.getMetadata().getName();
    String serviceType = service.getSpec().getType();
    String clusterIp = service.getSpec().getClusterIP();
    String externalIp = (service.getSpec().getExternalIPs() != null && !service.getSpec().getExternalIPs().isEmpty())
      ? service.getSpec().getExternalIPs().get(0)
      : "none";

    String[] ports = service.getSpec().getPorts().stream()
      .map(port -> port.getPort().toString())
      .toArray(String[]::new);

    // Age hesaplama (5d21h formatında)
    OffsetDateTime creationTimestamp = service.getMetadata().getCreationTimestamp();
    String age = "Unknown";
    if (creationTimestamp != null) {
      age = Util.formatDate(creationTimestamp);
    }

    return ServiceDTO.builder()
      .name(serviceName)
      .type(serviceType)
      .clusterIp(clusterIp)
      .externalIp(externalIp)
      .ports(ports)
      .age(age)
      .build();
  }
}
