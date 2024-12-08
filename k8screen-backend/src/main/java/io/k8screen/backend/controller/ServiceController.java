package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.ServiceDTO;
import io.k8screen.backend.service.ServiceService;
import io.kubernetes.client.openapi.models.V1Service;
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
@RequestMapping("/api/kubernetes/namespaces/{namespace}/services")
public class ServiceController {
  private final @NotNull ServiceService serviceService;

  public ServiceController(final @NotNull ServiceService serviceService) {
    this.serviceService = serviceService;
  }

  @PostMapping
  public ResponseEntity<V1Service> create(
      @PathVariable final @NotNull String namespace, @RequestBody final @NotNull V1Service service)
      throws Exception {
    final V1Service createdService = this.serviceService.create(namespace, service);
    return ResponseEntity.status(HttpStatus.OK).body(createdService);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Service> getService(
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Service service)
      throws Exception {
    final V1Service updatedService = this.serviceService.update(namespace, name, service);
    return ResponseEntity.status(HttpStatus.OK).body(updatedService);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getServiceDetail(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final String service = this.serviceService.getDetailByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @GetMapping
  public ResponseEntity<List<ServiceDTO>> listServices(
      @PathVariable final @NotNull String namespace) throws Exception {
    final List<ServiceDTO> services = this.serviceService.findAll(namespace);
    return ResponseEntity.status(HttpStatus.OK).body(services);
  }

  @GetMapping("/{name}")
  public ResponseEntity<ServiceDTO> getService(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final ServiceDTO service = this.serviceService.findByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(service);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Service> deleteSecret(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final V1Service status = this.serviceService.deleteByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
