package io.k8screen.backend.k8s.stateful_set;

import io.k8screen.backend.k8s.stateful_set.dto.StatefulSetInfo;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/v1/namespaces/{namespace}/stateful-sets")
@RequiredArgsConstructor
public class StatefulSetController {
  private final @NotNull StatefulSetService statefulSetService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping
  public @NotNull ResponseEntity<Result> createStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1StatefulSet statefulSet)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1StatefulSet createdStatefulSet =
        this.statefulSetService.create(namespace, statefulSet, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "statefulSetCreated", createdStatefulSet));
  }

  @PutMapping("/{name}")
  public @NotNull ResponseEntity<Result> updateStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1StatefulSet statefulSet)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1StatefulSet updatedStatefulSet =
        this.statefulSetService.update(namespace, name, statefulSet, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "statefulSetUpdated", updatedStatefulSet));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> listStatefulSet(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<StatefulSetInfo> services =
        this.statefulSetService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "statefulSetsFetched", services));
  }

  @GetMapping("/{name}")
  public @NotNull ResponseEntity<Result> getStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final StatefulSetInfo service =
        this.statefulSetService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "statefulSetFetched", service));
  }

  @GetMapping("/{name}/details")
  public @NotNull ResponseEntity<Result> getStatefulSetDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String statefulSet =
        this.statefulSetService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "statefulSetDetailFetched", statefulSet));
  }

  @DeleteMapping("/{name}")
  public @NotNull ResponseEntity<Result> deleteStatefulSet(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.statefulSetService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "statefulSetDeleted", status));
  }
}
