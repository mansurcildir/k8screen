package io.k8screen.backend.service;

import io.k8screen.backend.util.ApiClientFactory;
import io.k8screen.backend.data.dto.ServiceDTO;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.ServiceConverter;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Yaml;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull ServiceConverter serviceConverter;

  public V1Service create(
      final @NotNull String namespace,
      final @NotNull V1Service secret,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.createNamespacedService(namespace, secret).execute();
  }

  public V1Service update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1Service service,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.replaceNamespacedService(name, namespace, service).execute();
  }

  public List<ServiceDTO> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1ServiceList serviceList = coreV1Api.listNamespacedService(namespace).execute();
    return serviceList.getItems().stream().map(this.serviceConverter::toServiceDTO).toList();
  }

  public ServiceDTO findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Service service = coreV1Api.readNamespacedService(name, namespace).execute();
    return this.serviceConverter.toServiceDTO(service);
  }

  public String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    final V1Service service = coreV1Api.readNamespacedService(name, namespace).execute();
    if (service.getMetadata() != null && service.getMetadata().getManagedFields() != null) {
      service.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(service);
  }

  public V1Service deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final CoreV1Api coreV1Api = this.apiClientFactory.coreV1Api(user.config(), userId);
    return coreV1Api.deleteNamespacedService(name, namespace).execute();
  }
}
