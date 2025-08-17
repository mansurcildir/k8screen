package io.k8screen.backend.subscription;

import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.subscription.dto.SubscriptionPlanInfo;
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
@RequestMapping("/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionPlanController {

  private final @NotNull SubscriptionPlanService subscriptionPlanService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping
  public @NotNull ResponseEntity<Result> getActiveSubscriptionPlan(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final SubscriptionPlanInfo subscriptionPlanInfo =
        this.subscriptionPlanService.getActiveSubscription(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "activeSubscriptionFetched", subscriptionPlanInfo));
  }
}
