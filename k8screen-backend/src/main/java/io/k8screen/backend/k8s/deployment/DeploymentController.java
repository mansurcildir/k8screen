package io.k8screen.backend.k8s.deployment;

import io.k8screen.backend.k8s.deployment.dto.DeploymentInfo;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.kubernetes.client.openapi.models.V1Deployment;
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
@RequestMapping("/v1/namespaces/{namespace}/deployments")
@RequiredArgsConstructor
public class DeploymentController {

  private final @NotNull DeploymentService deploymentService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping
  public @NotNull ResponseEntity<Result> createDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    final V1Deployment createdDeployment =
        this.deploymentService.create(namespace, deployment, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "deploymentCreated", createdDeployment));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> listDeployments(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<DeploymentInfo> deployment =
        this.deploymentService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(HttpStatus.OK.value(), "deploymentsFetched", deployment));
  }

  @GetMapping("/{name}")
  public @NotNull ResponseEntity<Result> getDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final DeploymentInfo deployment =
        this.deploymentService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "deploymentFetched", deployment));
  }

  @GetMapping("/{name}/details")
  public @NotNull ResponseEntity<Result> getDeploymentDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String deployment =
        this.deploymentService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "deploymentDetailFetched", deployment));
  }

  @PutMapping("/{name}")
  public @NotNull ResponseEntity<Result> updateDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Deployment deployment)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Deployment updatedDeployment =
        this.deploymentService.updateByName(namespace, name, deployment, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "deploymentUpdated", deployment));
  }

  @DeleteMapping("/{name}")
  public @NotNull ResponseEntity<Result> deleteDeployment(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.deploymentService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "deploymentDeleted", status));
  }
}
