package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.k8s.ServiceInfo;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.mapper.ServiceConverter;
import io.k8screen.backend.repository.UserRepository;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Yaml;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull ServiceConverter serviceConverter;

  public @NotNull V1Service create(
      final @NotNull String namespace,
      final @NotNull V1Service secret,
      final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    return coreV1Api.createNamespacedService(namespace, secret).execute();
  }

  public @NotNull V1Service update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Service service,
      final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    return coreV1Api.replaceNamespacedService(name, namespace, service).execute();
  }

  public @NotNull List<ServiceInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1ServiceList serviceList = coreV1Api.listNamespacedService(namespace).execute();
    return serviceList.getItems().stream().map(this.serviceConverter::toServiceInfo).toList();
  }

  public @NotNull ServiceInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1Service service = coreV1Api.readNamespacedService(name, namespace).execute();
    return this.serviceConverter.toServiceInfo(service);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    final V1Service service = coreV1Api.readNamespacedService(name, namespace).execute();
    if (service.getMetadata() != null && service.getMetadata().getManagedFields() != null) {
      service.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(service);
  }

  public @NotNull V1Service deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.getActiveConfig(), userUuid);
    return coreV1Api.deleteNamespacedService(name, namespace).execute();
  }
}
