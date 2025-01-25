package io.k8screen.backend.service;

import io.k8screen.backend.util.ApiClientFactory;
import io.k8screen.backend.data.dto.StatefulSetDTO;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.StatefulSetConverter;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class StatefulSetService {

  private final @NotNull UserService userService;
  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull StatefulSetConverter statefulSetConverter;

  public V1StatefulSet create(
      final @NotNull String namespace,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.createNamespacedStatefulSet(namespace, statefulSet).execute();
  }

  public V1StatefulSet update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.replaceNamespacedStatefulSet(name, namespace, statefulSet).execute();
  }

  public List<StatefulSetDTO> findAll(final @NotNull String namespace, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1StatefulSetList serviceList = appsV1Api.listNamespacedStatefulSet(namespace).execute();
    return serviceList.getItems().stream()
        .map(this.statefulSetConverter::toStatefulSetDTO)
        .toList();
  }

  public StatefulSetDTO findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1StatefulSet statefulSet =
        appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    return this.statefulSetConverter.toStatefulSetDTO(statefulSet);
  }

  public String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    final V1StatefulSet statefulSet =
        appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    if (statefulSet.getMetadata() != null && statefulSet.getMetadata().getManagedFields() != null) {
      statefulSet.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(statefulSet);
  }

  public V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull String userId)
      throws Exception {
    final UserItem user = this.userService.findById(userId);
    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.config(), userId);
    return appsV1Api.deleteNamespacedStatefulSet(name, namespace).execute();
  }
}
