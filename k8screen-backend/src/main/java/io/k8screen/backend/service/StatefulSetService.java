package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.StatefulSetDTO;
import io.k8screen.backend.mapper.StatefulSetConverter;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatefulSetService {

  private final @NotNull AppsV1Api appsV1Api;
  private final @NotNull StatefulSetConverter statefulSetConverter;

  public StatefulSetService(final @NotNull AppsV1Api appsV1Api, final @NotNull StatefulSetConverter statefulSetConverter) {
    this.appsV1Api = appsV1Api;
    this.statefulSetConverter = statefulSetConverter;
  }

  public V1StatefulSet create(final @NotNull String namespace, final @NotNull V1StatefulSet statefulSet)
    throws Exception {
    return this.appsV1Api.createNamespacedStatefulSet(namespace, statefulSet).execute();
  }

  public V1StatefulSet update(
    final @NotNull String namespace, final @NotNull String name, final @NotNull V1StatefulSet statefulSet)
    throws Exception {
    return this.appsV1Api.replaceNamespacedStatefulSet(name, namespace, statefulSet).execute();
  }

  public List<StatefulSetDTO> findAll(final @NotNull String namespace) throws Exception {
    final V1StatefulSetList serviceList = this.appsV1Api.listNamespacedStatefulSet(namespace).execute();
    return serviceList.getItems().stream().map(this.statefulSetConverter::toStatefulSetDTO).toList();
  }

  public StatefulSetDTO findByName(final @NotNull String namespace, final @NotNull String name)
    throws Exception {

    V1StatefulSet statefulSet = this.appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    return this.statefulSetConverter.toStatefulSetDTO(statefulSet);

  }

  public String getDetailByName(final @NotNull String namespace, final @NotNull String name)
    throws Exception {
    V1StatefulSet statefulSet = this.appsV1Api.readNamespacedStatefulSet(name, namespace).execute();
    if (statefulSet.getMetadata() != null && statefulSet.getMetadata().getManagedFields() != null) {
      statefulSet.getMetadata().setManagedFields(null);
    }
    return Yaml.dump(statefulSet);
  }

  public V1Status deleteByName(final @NotNull String namespace, final @NotNull String name)
    throws Exception {
    return this.appsV1Api.deleteNamespacedStatefulSet(name, namespace).execute();
  }
}
