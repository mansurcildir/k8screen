package io.k8screen.backend.k8s.secret;

import io.k8screen.backend.k8s.secret.dto.SecretInfo;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.kubernetes.client.openapi.models.V1Secret;
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
@RequestMapping("/v1/namespaces/{namespace}/secrets")
@RequiredArgsConstructor
public class SecretController {

  private final @NotNull SecretService secretService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping
  public @NotNull ResponseEntity<Result> createSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Secret createdSecret =
        this.secretService.create(namespace, secret, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretCreated", createdSecret));
  }

  @PutMapping("/{name}")
  public @NotNull ResponseEntity<Result> updateSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Secret updatedSecret =
        this.secretService.update(namespace, name, secret, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretUpdated", updatedSecret));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> listSecrets(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<SecretInfo> secrets = this.secretService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretsFetched", secrets));
  }

  @GetMapping("/{name}")
  public @NotNull ResponseEntity<Result> getSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final SecretInfo secret =
        this.secretService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretFetched", secret));
  }

  @GetMapping("/{name}/details")
  public @NotNull ResponseEntity<Result> getSecretsDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String secret =
        this.secretService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretDetailFetched", secret));
  }

  @DeleteMapping("/{name}")
  public @NotNull ResponseEntity<Result> deleteSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.secretService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "secretDeleted", status));
  }
}
