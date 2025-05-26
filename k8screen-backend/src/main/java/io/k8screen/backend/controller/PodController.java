package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.k8s.PodInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.PodService;
import io.kubernetes.client.openapi.models.V1Pod;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/namespaces/{namespace}/pods")
public class PodController {

  private final @NotNull PodService podService;

  public PodController(final @NotNull PodService podService) {
    this.podService = podService;
  }

  @GetMapping
  public ResponseEntity<List<PodInfo>> listPods(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<PodInfo> pods = this.podService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(pods);
  }

  @GetMapping("/{name}")
  public ResponseEntity<PodInfo> getPod(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final PodInfo pod = this.podService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getPodDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String pod = this.podService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Pod> deletePod(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Pod pod = this.podService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }
}
