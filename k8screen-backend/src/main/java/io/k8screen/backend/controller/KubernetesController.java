package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.KubernetesService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/kubernetes")
public class KubernetesController {

  private final @NotNull KubernetesService kubernetesService;

  public KubernetesController(final @NotNull KubernetesService kubernetesService) {
    this.kubernetesService = kubernetesService;
  }

  @GetMapping("/nodes")
  public ResponseEntity<List<String>> listNodes(final @NotNull Authentication authentication)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<String> nodes = this.kubernetesService.getNodes(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(nodes);
  }
}
