package io.k8screen.backend.k8s.service;

import io.k8screen.backend.k8s.service.dto.ServiceInfo;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.kubernetes.client.openapi.models.V1Service;
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
@RequestMapping("/v1/namespaces/{namespace}/services")
@RequiredArgsConstructor
public class ServiceController {
  private final @NotNull ServiceService serviceService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping
  public @NotNull ResponseEntity<Result> createService(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Service service)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service createdService =
        this.serviceService.create(namespace, service, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(HttpStatus.OK.value(), "serviceCreated", createdService));
  }

  @PutMapping("/{name}")
  public @NotNull ResponseEntity<Result> updateService(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Service service)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service updatedService =
        this.serviceService.update(namespace, name, service, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(HttpStatus.OK.value(), "serviceUpdated", updatedService));
  }

  @GetMapping("/{name}/details")
  public @NotNull ResponseEntity<Result> getServiceDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String service =
        this.serviceService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "serviceDetailFetched", service));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> listServices(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<ServiceInfo> services =
        this.serviceService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "servicesFetched", services));
  }

  @GetMapping("/{name}")
  public @NotNull ResponseEntity<Result> getService(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final ServiceInfo service =
        this.serviceService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "serviceFetched", service));
  }

  @DeleteMapping("/{name}")
  public @NotNull ResponseEntity<Result> deleteSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service status =
        this.serviceService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "serviceDeleted", status));
  }
}
