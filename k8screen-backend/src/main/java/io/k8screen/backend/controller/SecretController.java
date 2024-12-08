package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.SecretDTO;
import io.k8screen.backend.service.SecretService;
import io.kubernetes.client.openapi.models.V1Secret;
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
@RequestMapping("/api/kubernetes/namespaces/{namespace}/secrets")
public class SecretController {

  private final @NotNull SecretService secretService;

  public SecretController(final @NotNull SecretService secretService) {
    this.secretService = secretService;
  }

  @GetMapping
  public ResponseEntity<List<SecretDTO>> listSecrets(@PathVariable final @NotNull String namespace)
      throws Exception {
    final List<SecretDTO> secrets = this.secretService.findAll(namespace);
    return ResponseEntity.status(HttpStatus.OK).body(secrets);
  }

  @GetMapping("/{name}")
  public ResponseEntity<SecretDTO> getSecret(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final SecretDTO secret = this.secretService.findByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(secret);
  }

  @GetMapping("/{name}/details")
  public ResponseEntity<String> getSecretsDetail(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final String secret = this.secretService.getDetailByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(secret);
  }

  @PostMapping
  public ResponseEntity<V1Secret> createSecret(
      @PathVariable final @NotNull String namespace, @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final V1Secret createdSecret = this.secretService.create(namespace, secret);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdSecret);
  }

  @PutMapping("/{name}")
  public ResponseEntity<V1Secret> updateSecret(
      @PathVariable final @NotNull String namespace,
      @PathVariable final @NotNull String name,
      @RequestBody final @NotNull V1Secret secret)
      throws Exception {
    final V1Secret createdSecret = this.secretService.updateByName(namespace, name, secret);
    return ResponseEntity.status(HttpStatus.OK).body(createdSecret);
  }

  @DeleteMapping("/{name}")
  public ResponseEntity<V1Status> deleteSecret(
      @PathVariable final @NotNull String namespace, @PathVariable final @NotNull String name)
      throws Exception {
    final V1Status status = this.secretService.deleteByName(namespace, name);
    return ResponseEntity.status(HttpStatus.OK).body(status);
  }
}
