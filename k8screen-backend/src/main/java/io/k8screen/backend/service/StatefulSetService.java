package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.k8s.StatefulSetInfo;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.mapper.StatefulSetConverter;
import io.k8screen.backend.repository.UserRepository;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1Status;
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
public class StatefulSetService {

  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull UserRepository userRepository;
  private final @NotNull StatefulSetConverter statefulSetConverter;

  public @NotNull V1StatefulSet create(
      final @NotNull String namespace,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    return appsV1Api.createNamespacedStatefulSet(namespace, statefulSet).execute();
  }

  public @NotNull V1StatefulSet update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    return appsV1Api.replaceNamespacedStatefulSet(name, namespace, statefulSet).execute();
  }

  public @NotNull List<StatefulSetInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    final V1StatefulSetList serviceList = appsV1Api.listNamespacedStatefulSet(namespace).execute();
    return serviceList.getItems().stream()
        .map(this.statefulSetConverter::toStatefulSetDTO)
        .toList();
  }

  public @NotNull StatefulSetInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    final V1StatefulSet statefulSet =
        appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    return this.statefulSetConverter.toStatefulSetDTO(statefulSet);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    final V1StatefulSet statefulSet =
        appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    if (statefulSet.getMetadata() != null && statefulSet.getMetadata().getManagedFields() != null) {
      statefulSet.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(statefulSet);
  }

  public @NotNull V1Status deleteByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(user.getActiveConfig(), userUuid);
    return appsV1Api.deleteNamespacedStatefulSet(name, namespace).execute();
  }
}
