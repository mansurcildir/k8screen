package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.ServiceDTO;
import io.k8screen.backend.mapper.ServiceConverter;
import io.k8screen.backend.util.Util;
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
  private final @NotNull ServiceConverter serviceConverter;

  public ServiceService(final @NotNull CoreV1Api coreV1Api, final @NotNull ServiceConverter serviceConverter) {
    this.coreV1Api = coreV1Api;
    this.serviceConverter = serviceConverter;
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

  public List<ServiceDTO> findAll(final @NotNull String namespace) throws Exception {
    final V1ServiceList serviceList = this.coreV1Api.listNamespacedService(namespace).execute();
    return serviceList.getItems().stream().map(this.serviceConverter::toServiceDTO).toList();
  }

  public ServiceDTO findByName(final @NotNull String namespace, final @NotNull String name)
    throws Exception {

    V1Service service = this.coreV1Api.readNamespacedService(name, namespace).execute();
    return this.serviceConverter.toServiceDTO(service);

  }

  public V1Service deleteByName(final @NotNull String namespace, final @NotNull String name)
      throws Exception {
    return this.coreV1Api.deleteNamespacedService(name, namespace).execute();
  }
}
