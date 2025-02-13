package io.k8screen.backend.controller;

import io.k8screen.backend.service.KubernetesService;
import io.k8screen.backend.util.CustomUserDetails;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kubernetes")
public class KubernetesController {

  private final @NotNull KubernetesService kubernetesService;

  public KubernetesController(final @NotNull KubernetesService kubernetesService) {
    this.kubernetesService = kubernetesService;
  }

  @GetMapping("/nodes")
  public ResponseEntity<List<String>> listNodes(final @NotNull Authentication authentication)
      throws Exception {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final List<String> nodes = this.kubernetesService.getNodes(userId);
    return ResponseEntity.status(HttpStatus.OK).body(nodes);
  }
}
