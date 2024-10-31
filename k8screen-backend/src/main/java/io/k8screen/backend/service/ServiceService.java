package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.ServiceDTO;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {

  private final @NotNull CoreV1Api coreV1Api;

  public ServiceService(final @NotNull CoreV1Api coreV1Api) {
    this.coreV1Api = coreV1Api;
  }

  public V1Service create(final @NotNull String namespace, final @NotNull V1Service secret)
      throws Exception {
    return this.coreV1Api.createNamespacedService(namespace, secret).execute();
  }

  public V1Service update(
      final @NotNull String namespace, final @NotNull String name, final @NotNull V1Service service)
      throws Exception {
    return this.coreV1Api.replaceNamespacedService(name, namespace, service).execute();
  }

  public List<V1Service> findAll(final @NotNull String namespace) throws Exception {
    final V1ServiceList serviceList = this.coreV1Api.listNamespacedService(namespace).execute();
    return serviceList.getItems();
  }

  public ServiceDTO findByName(final @NotNull String namespace, final @NotNull String name)
    throws Exception {

    V1Service service = this.coreV1Api.readNamespacedService(name, namespace).execute();

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
      Duration duration = Duration.between(creationTimestamp, OffsetDateTime.now());

      long days = duration.toDays();
      long hours = duration.toHours() % 24;
      long minutes = duration.toMinutes() % 60;
      long seconds = duration.getSeconds() % 60;

      StringBuilder ageBuilder = new StringBuilder();

      if (days > 0) {
        ageBuilder.append(days).append("d");
      }
      if (hours > 0 || ageBuilder.length() > 0) { // Eğer gün varsa, saat göster
        ageBuilder.append(hours).append("h");
      }
      if (minutes > 0 || ageBuilder.length() > 0) { // Eğer saat veya gün varsa, dakika göster
        ageBuilder.append(minutes).append("m");
      }
      if (seconds > 0 || ageBuilder.length() == 0) { // Eğer hiçbir şey yoksa, saniye göster
        ageBuilder.append(seconds).append("s");
      }

      age = ageBuilder.toString();
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

  public V1Service deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.deleteNamespacedService(name, namespace).execute();
  }
}
