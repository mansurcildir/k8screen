package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.k8s.ServiceInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.ServiceService;
import io.kubernetes.client.openapi.models.V1Service;
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
@RequestMapping("/v1/namespaces/{namespace}/services")
public class ServiceController {
  private final @NotNull ServiceService serviceService;

  public ServiceController(final @NotNull ServiceService serviceService) {
    this.serviceService = serviceService;
  }

  @PostMapping
  public ResponseEntity<V1Service> create(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Service service)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service createdService =
        this.serviceService.create(namespace, service, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(createdService);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Service> getService(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Service service)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service updatedService =
        this.serviceService.update(namespace, name, service, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(updatedService);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getServiceDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String service =
        this.serviceService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @GetMapping
  public ResponseEntity<List<ServiceInfo>> listServices(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<ServiceInfo> services =
        this.serviceService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(services);
  }

  @GetMapping("/{name}")
  public ResponseEntity<ServiceInfo> getService(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final ServiceInfo service =
        this.serviceService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Service> deleteSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Service status =
        this.serviceService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
