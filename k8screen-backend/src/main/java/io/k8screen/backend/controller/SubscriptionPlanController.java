package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.subscription.SubscriptionPlanInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionPlanController {

  private final @NotNull SubscriptionPlanService subscriptionPlanService;

  @GetMapping
  public ResponseEntity<SubscriptionPlanInfo> getStripePanelSession(
      final @NotNull Authentication authentication) throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final SubscriptionPlanInfo subscriptionPlanInfo =
        this.subscriptionPlanService.getActiveSubscription(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(subscriptionPlanInfo);
  }
}
