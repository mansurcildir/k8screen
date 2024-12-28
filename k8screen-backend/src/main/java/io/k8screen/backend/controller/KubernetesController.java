package io.k8screen.backend.controller;

import io.k8screen.backend.service.KubernetesService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesController {

  private final @NotNull KubernetesService kubernetesService;

  public KubernetesController(final @NotNull KubernetesService kubernetesService) {
    this.kubernetesService = kubernetesService;
  }

  @GetMapping("nodes")
  public ResponseEntity<List<String>> listNodes() throws Exception {
    final List<String> nodes = this.kubernetesService.getNodes();
    return ResponseEntity.status(HttpStatus.OK).body(nodes);
  }
}

