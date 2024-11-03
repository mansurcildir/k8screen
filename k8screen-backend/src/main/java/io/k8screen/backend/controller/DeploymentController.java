package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.DeploymentDTO;
import io.k8screen.backend.service.DeploymentService;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kubernetes/namespaces/{namespace}/deployments")
public class DeploymentController {

  private final @NotNull DeploymentService deploymentService;

  public DeploymentController(final @NotNull DeploymentService deploymentService) {
    this.deploymentService = deploymentService;
  }

  @PostMapping
  public ResponseEntity<V1Deployment> createDeployment(
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final V1Deployment createdDeployment = this.deploymentService.create(namespace, deployment);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDeployment);
  }

  @GetMapping
  public ResponseEntity<List<DeploymentDTO>> listDeployments(
      @PathVariable final @NotNull String namespace) throws Exception {
    final List<DeploymentDTO> deployment = this.deploymentService.findAll(namespace);
    return ResponseEntity.status(HttpStatus.OK).body(deployment);
  }

  @GetMapping("/{name}")
  public ResponseEntity<DeploymentDTO> getDeployment(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final DeploymentDTO deployment = this.deploymentService.findByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(deployment);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Deployment> updateDeployment(
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final V1Deployment updatedDeployment =
        this.deploymentService.updateByName(namespace, name, deployment);
    return ResponseEntity.status(HttpStatus.OK).body(updatedDeployment);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Status> deletePod(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final V1Status status = this.deploymentService.deleteByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
