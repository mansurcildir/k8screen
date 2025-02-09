package io.k8screen.backend.controller;

import io.k8screen.backend.service.NamespaceService;
import io.k8screen.backend.util.CustomUserDetails;
import io.kubernetes.client.openapi.models.V1Namespace;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/namespaces")
public class NamespaceController {

  private final @NotNull NamespaceService namespaceService;

  public NamespaceController(final @NotNull NamespaceService namespaceService) {
    this.namespaceService = namespaceService;
  }

  @GetMapping
  public ResponseEntity<List<String>> listNamespaces(final @NotNull Authentication authentication)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final List<String> namespaces = this.namespaceService.getAllNamespaces(userId);
    return ResponseEntity.status(HttpStatus.OK).body(namespaces);
  }

  @PostMapping
  public ResponseEntity<V1Namespace> createNamespace(
      final @NotNull Authentication authentication, @RequestBody final @NotNull String namespace)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final V1Namespace createdNamespace = this.namespaceService.createNamespace(namespace, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdNamespace);
  }

  @DeleteMapping("/{namespace}")
  public ResponseEntity<V1Status> deleteNamespace(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final V1Status status = this.namespaceService.deleteNamespace(namespace, userId);
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
