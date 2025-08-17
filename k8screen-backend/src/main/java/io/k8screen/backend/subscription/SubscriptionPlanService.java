package io.k8screen.backend.subscription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.k8s.config.ConfigRepository;
import io.k8screen.backend.subscription.dto.SubscriptionPlanInfo;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionPlanService {

  @Value("${stripe.product.price-id.free}")
  private String stripeFreeProductPriceId;

  @Value("${stripe.product.price-id.standard}")
  private String stripeStandardProductPriceId;

  @Value("${stripe.product.price-id.premium}")
  private String stripePremiumProductPriceId;

  private static final @NotNull String SUBSCRIPTION_PLAN_FREE = "Free";
  private static final @NotNull String SUBSCRIPTION_PLAN_STANDARD = "Standard";
  private static final @NotNull String SUBSCRIPTION_PLAN_PREMIUM = "Premium";
  private static final @NotNull String EVENT_INVOICE_PAID = "invoice.paid";

  private final @NotNull UserRepository userRepository;
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull SubscriptionPlanRepository subscriptionPlanRepository;
  private final @NotNull SubscriptionPlanConverter subscriptionPlanConverter;

  public @NotNull SubscriptionPlanInfo getActiveSubscription(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final SubscriptionPlan subscriptionPlan =
        this.subscriptionPlanRepository
            .findByName(user.getSubscriptionPlan().getName())
            .orElseThrow(() -> new ItemNotFoundException("subscriptionPlanNotFound"));

    return this.subscriptionPlanConverter.toSubscriptionPlanInfo(subscriptionPlan);
  }

  public void handleSubscriptionEvent(final @NotNull Event event)
      throws StripeException, BadRequestException {
    final var dataObjectDeserializer = event.getDataObjectDeserializer();
    final String eventType = event.getType();

    if (!eventType.equals(EVENT_INVOICE_PAID)) {
      log.error("Unexpected stripe webhook event type {} ({})", eventType, event.getId());
      throw new BadRequestException("unexpectedEventType");
    }

    final String customerId = this.extractCustomerId(dataObjectDeserializer);
    final String subscriptionId = this.extractSubscriptionId(dataObjectDeserializer);

    this.replaceSubscription(customerId, subscriptionId);
  }

  private @NotNull SubscriptionPlan getSubscriptionPlan(final @NotNull Subscription subscription) {
    var subscriptionPlanOptional = Optional.<SubscriptionPlan>empty();
    final Price priceData = subscription.getItems().getData().getFirst().getPrice();

    if (priceData.getId().equalsIgnoreCase(this.stripeFreeProductPriceId)) {
      subscriptionPlanOptional = this.subscriptionPlanRepository.findByName(SUBSCRIPTION_PLAN_FREE);
    } else if (priceData.getId().equalsIgnoreCase(this.stripeStandardProductPriceId)) {
      subscriptionPlanOptional =
          this.subscriptionPlanRepository.findByName(SUBSCRIPTION_PLAN_STANDARD);
    } else if (priceData.getId().equalsIgnoreCase(this.stripePremiumProductPriceId)) {
      subscriptionPlanOptional =
          this.subscriptionPlanRepository.findByName(SUBSCRIPTION_PLAN_PREMIUM);
    }

    return subscriptionPlanOptional.orElseThrow(
        () -> new ItemNotFoundException("subscriptionPlanNotFound"));
  }

  private void replaceSubscription(
      final @NotNull String customerId, final @NotNull String subscriptionId)
      throws StripeException {
    final Subscription subscription = Subscription.retrieve(subscriptionId);

    final User user =
        this.userRepository
            .findByStripeCustomerId(UUID.fromString(customerId))
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final SubscriptionPlan subscriptionPlan = this.getSubscriptionPlan(subscription);

    if (!subscriptionPlan.equals(user.getSubscriptionPlan())) {
      user.setSubscriptionPlan(subscriptionPlan);
      this.userRepository.save(user);
      this.deleteRemainingConfigs(subscriptionPlan.getMaxConfigCount(), user);
    }
  }

  private void deleteRemainingConfigs(final long limit, final @NotNull User user) {
    final long configCount = this.configRepository.countConfigByUserUuid(user.getUuid());

    if (configCount > limit) {
      final int diff = (int) (configCount - limit);
      final List<Long> configIds =
          this.configRepository.findIdsByUserIdOrderByCreatedAtDesc(
              PageRequest.of(0, diff), user.getId());
      this.configRepository.deleteAllByIdsAndUserId(configIds, user.getId());
    }
  }

  private @NotNull String extractCustomerId(
      final @NotNull EventDataObjectDeserializer dataObjectDeserializer)
      throws BadRequestException {
    final String rawJson = dataObjectDeserializer.getRawJson();
    if (rawJson == null) {
      throw new BadRequestException("rawJsonNotExists");
    }

    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonNode jsonNode = objectMapper.readTree(rawJson);

      final JsonNode customerNode = jsonNode.path("customer");

      if (!customerNode.isMissingNode() && !customerNode.isNull()) {
        return customerNode.asText();
      }

      throw new BadRequestException("customerIdNotExist");

    } catch (IOException e) {
      throw new BadRequestException("invalidRawJson");
    }
  }

  private @NotNull String extractSubscriptionId(
      final @NotNull EventDataObjectDeserializer dataObjectDeserializer)
      throws BadRequestException {

    final String rawJson = dataObjectDeserializer.getRawJson();
    if (rawJson == null) {
      throw new BadRequestException("rawJsonNotExists");
    }

    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonNode jsonNode = objectMapper.readTree(rawJson);

      final JsonNode subscriptionNode =
          jsonNode.path("parent").path("subscription_details").path("subscription");

      if (!subscriptionNode.isMissingNode() && !subscriptionNode.isNull()) {
        return subscriptionNode.asText();
      }

      throw new BadRequestException("subscriptionIdNotExist");

    } catch (final @NotNull IOException e) {
      throw new BadRequestException("invalidRawJson");
    }
  }
}
