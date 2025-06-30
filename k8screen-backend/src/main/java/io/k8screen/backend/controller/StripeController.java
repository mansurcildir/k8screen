package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.StripeService;
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

  @GetMapping("/panel")
  public ResponseEntity<String> getStripePanelSession(final @NotNull Authentication authentication)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final String sessionUrl = this.stripeService.createStripePanelSession(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(sessionUrl);
  }
}
