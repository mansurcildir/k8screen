package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.k8s.StatefulSetInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.StatefulSetService;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/namespaces/{namespace}/stateful-sets")
public class StatefulSetController {
  private final @NotNull StatefulSetService statefulSetService;

  public StatefulSetController(final @NotNull StatefulSetService statefulSetService) {
    this.statefulSetService = statefulSetService;
  }

  @PostMapping
  public ResponseEntity<V1StatefulSet> create(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1StatefulSet statefulSet)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1StatefulSet createdStatefulSet =
        this.statefulSetService.create(namespace, statefulSet, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(createdStatefulSet);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1StatefulSet> getStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1StatefulSet statefulSet)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1StatefulSet updatedService =
        this.statefulSetService.update(namespace, name, statefulSet, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(updatedService);
  }

  @GetMapping
  public ResponseEntity<List<StatefulSetInfo>> listStatefulSet(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<StatefulSetInfo> service =
        this.statefulSetService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @GetMapping("/{name}")
  public ResponseEntity<StatefulSetInfo> getStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final StatefulSetInfo service =
        this.statefulSetService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getStatefulSetDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String statefulSet =
        this.statefulSetService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(statefulSet);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Status> deleteStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.statefulSetService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
