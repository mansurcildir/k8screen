package io.k8screen.backend.controller;

import io.k8screen.backend.service.PodService;
import io.kubernetes.client.openapi.models.V1Pod;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kubernetes/namespaces/{namespace}/pods")
public class PodController {

  private final @NotNull PodService podService;

  public PodController(final @NotNull PodService podService) {
    this.podService = podService;
  }

  @GetMapping
  public ResponseEntity<List<V1Pod>> listPods(@PathVariable final @NotNull String namespace)
      throws Exception {
    final List<V1Pod> pods = this.podService.findAll(namespace);
    return ResponseEntity.status(HttpStatus.OK).body(pods);
  }

  @GetMapping("/{name}")
  public ResponseEntity<V1Pod> getPod(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final V1Pod pod = this.podService.findByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Pod> deletePod(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final V1Pod pod = this.podService.deleteByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @GetMapping("/{name}/logs")
  public ResponseEntity<String> getLogs(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final String pod = this.podService.findLogs(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(pod);
  }

  @PostMapping("/{name}/exec")
  public ResponseEntity<String> exec(
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull String[] command)
      throws Exception {
    final String response = this.podService.exec(namespace, name, command);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
