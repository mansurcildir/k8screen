package io.k8screen.backend.controller;

import io.k8screen.backend.config.CustomUserDetails;
import io.k8screen.backend.data.dto.PodDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<List<PodDTO>> listPods(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final List<PodDTO> pods = this.podService.findAll(namespace, userId);
    return ResponseEntity.status(HttpStatus.OK).body(pods);
  }

  @GetMapping("/{name}")
  public ResponseEntity<PodDTO> getPod(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final PodDTO pod = this.podService.findByName(namespace, name, userId);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getPodDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final String pod = this.podService.getDetailByName(namespace, name, userId);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Pod> deletePod(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final V1Pod pod = this.podService.deleteByName(namespace, name, userId);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @GetMapping("/{name}/logs")
  public ResponseEntity<String> getLogs(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final String pod = this.podService.findLogs(namespace, name, userId);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @PostMapping("/{name}/exec")
  public ResponseEntity<String> exec(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull String[] command)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final String response = this.podService.exec(namespace, name, command, userId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
