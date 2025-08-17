package io.k8screen.backend.k8s.node;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/nodes")
@RequiredArgsConstructor
public class NodeController {

  private final @NotNull NodeService nodeService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping
  public @NotNull ResponseEntity<Result> listNodes(final @NotNull Authentication authentication)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<String> nodes = this.nodeService.getNodes(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "nodesFetched", nodes));
  }
}
