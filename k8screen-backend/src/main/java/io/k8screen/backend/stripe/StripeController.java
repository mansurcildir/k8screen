package io.k8screen.backend.stripe;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stripe")
@RequiredArgsConstructor
public class StripeController {

  private final @NotNull StripeService stripeService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping("/panel")
  public @NotNull ResponseEntity<Result> getStripePanelSession(
      final @NotNull Authentication authentication) throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String sessionUrl = this.stripeService.createStripePanelSession(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(HttpStatus.OK.value(), "panelSessionFetched", sessionUrl));
  }
}
