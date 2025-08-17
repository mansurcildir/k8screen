package io.k8screen.backend.k8s.stateful_set;

import io.k8screen.backend.k8s.stateful_set.dto.StatefulSetInfo;
import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.util.ApiClientFactory;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StatefulSetService {

  private final @NotNull ApiClientFactory apiClientFactory;
  private final @NotNull UserService userService;
  private final @NotNull StatefulSetConverter statefulSetConverter;

  public @NotNull V1StatefulSet create(
      final @NotNull String namespace,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.createNamespacedStatefulSet(namespace, statefulSet).execute();
  }

  public @NotNull V1StatefulSet update(
      final @NotNull String namespace,
      final @NotNull String name,
      final @NotNull V1StatefulSet statefulSet,
      final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.replaceNamespacedStatefulSet(name, namespace, statefulSet).execute();
  }

  public @NotNull List<StatefulSetInfo> findAll(
      final @NotNull String namespace, final @NotNull UUID userUuid) throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    final V1StatefulSetList serviceList = appsV1Api.listNamespacedStatefulSet(namespace).execute();
    return serviceList.getItems().stream()
        .map(this.statefulSetConverter::toStatefulSetDTO)
        .toList();
  }

  public @NotNull StatefulSetInfo findByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    final V1StatefulSet statefulSet =
        appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    return this.statefulSetConverter.toStatefulSetDTO(statefulSet);
  }

  public @NotNull String getDetailByName(
      final @NotNull String namespace, final @NotNull String name, final @NotNull UUID userUuid)
      throws Exception {
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
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
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);

    final AppsV1Api appsV1Api = this.apiClientFactory.appsV1Api(userInfo.activeConfig(), userUuid);
    return appsV1Api.deleteNamespacedStatefulSet(name, namespace).execute();
  }
}
