package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.k8s.SecretInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.SecretService;
import io.kubernetes.client.openapi.models.V1Secret;
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
@RequestMapping("/v1/namespaces/{namespace}/secrets")
public class SecretController {

  private final @NotNull SecretService secretService;

  public SecretController(final @NotNull SecretService secretService) {
    this.secretService = secretService;
  }

  @GetMapping
  public ResponseEntity<List<SecretInfo>> listSecrets(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String namespace)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<SecretInfo> secrets = this.secretService.findAll(namespace, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(secrets);
  }

  @GetMapping("/{name}")
  public ResponseEntity<SecretInfo> getSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final SecretInfo secret =
        this.secretService.findByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(secret);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getSecretsDetail(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String secret =
        this.secretService.getDetailByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(secret);
  }

  @PostMapping
  public ResponseEntity<V1Secret> createSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Secret createdSecret =
        this.secretService.create(namespace, secret, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdSecret);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Secret> updateSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Secret createdSecret =
        this.secretService.updateByName(namespace, name, secret, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(createdSecret);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Status> deleteSecret(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final V1Status status =
        this.secretService.deleteByName(namespace, name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
