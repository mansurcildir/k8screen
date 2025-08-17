package io.k8screen.backend.k8s.namespace;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.kubernetes.client.openapi.models.V1Namespace;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/namespaces")
@RequiredArgsConstructor
public class NamespaceController {

  private final @NotNull NamespaceService namespaceService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping
  public @NotNull ResponseEntity<Result> listNamespaces(
      final @NotNull Authentication authentication) throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<String> namespaces = this.namespaceService.getAllNamespaces(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "namespacesFetched", namespaces));
  }

  @PostMapping
  public @NotNull ResponseEntity<Result> createNamespace(
      final @NotNull Authentication authentication, @RequestBody final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Namespace createdNamespace =
        this.namespaceService.createNamespace(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "namespaceCreated", createdNamespace));
  }

  @DeleteMapping("/{namespace}")
  public @NotNull ResponseEntity<Result> deleteNamespace(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.namespaceService.deleteNamespace(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "namespaceDeleted", status));
  }
}
