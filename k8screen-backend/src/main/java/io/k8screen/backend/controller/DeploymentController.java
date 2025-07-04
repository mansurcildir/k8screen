package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.k8s.DeploymentInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.DeploymentService;
import io.kubernetes.client.openapi.models.V1Deployment;
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
@RequestMapping("/v1/namespaces/{namespace}/deployments")
public class DeploymentController {

  private final @NotNull DeploymentService deploymentService;

  public DeploymentController(final @NotNull DeploymentService deploymentService) {
    this.deploymentService = deploymentService;
  }

  @PostMapping
  public ResponseEntity<V1Deployment> createDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    final V1Deployment createdDeployment =
        this.deploymentService.create(namespace, deployment, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDeployment);
  }

  @GetMapping
  public ResponseEntity<List<DeploymentInfo>> listDeployments(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<DeploymentInfo> deployment =
        this.deploymentService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(deployment);
  }

  @GetMapping("/{name}")
  public ResponseEntity<DeploymentInfo> getDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final DeploymentInfo deployment =
        this.deploymentService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(deployment);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getDeploymentDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String deployment =
        this.deploymentService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(deployment);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Deployment> updateDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Deployment updatedDeployment =
        this.deploymentService.updateByName(namespace, name, deployment, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(updatedDeployment);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Status> deletePod(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.deploymentService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
