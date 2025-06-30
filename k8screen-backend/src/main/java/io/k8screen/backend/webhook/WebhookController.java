package io.k8screen.backend.webhook;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {
  @Value("${stripe.webhook.signature-key}")
  private String webhookSecretKey;

  private final @NotNull SubscriptionPlanService subscriptionPlanService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping({"/stripe", "/stripe/"})
  public @NotNull ResponseEntity<Result> subscriptionChange(
      @RequestBody @Valid final @NotNull String payload,
      @RequestHeader("Stripe-Signature") final @NotNull String signatureHeader)
      throws Exception {

    final Event event = Webhook.constructEvent(payload, signatureHeader, this.webhookSecretKey);
    this.subscriptionPlanService.handleSubscriptionEvent(event);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "webhookInvoked"));
  }
}
