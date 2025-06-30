package io.k8screen.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.billingportal.Session;
import com.stripe.param.billingportal.SessionCreateParams;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.StripeIsDisabledException;
import io.k8screen.backend.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeService {

  @Value("${stripe.enable:false}")
  private boolean enableStripe;

  @Value("${stripe.secret-key}")
  private String stripeSecretKey;

  @Value("${stripe.product.price-id.free}")
  private String stripeFreeProductPriceId;

  private static final @NotNull String STRIPE_RETURN_URL = "http://localhost:5173";
  private final @NotNull UserRepository userRepository;

  @PostConstruct
  private void initStripe() {
    Stripe.apiKey = this.stripeSecretKey;
  }

  public @NotNull String createStripePanelSession(final @NotNull UUID userUuid)
      throws StripeException {
    if (!this.enableStripe) {
      throw new StripeIsDisabledException();
    }

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new StripeIsDisabledException("accessDenied"));

    final SessionCreateParams params =
        SessionCreateParams.builder()
            .setCustomer(user.getStripeCustomerId().toString())
            .setReturnUrl(STRIPE_RETURN_URL)
            .build();

    return Session.create(params).getUrl();
  }

  public void createCustomerWithFreeProduct(final @NotNull UUID userUuid) throws StripeException {
    if (!this.enableStripe) {
      throw new StripeIsDisabledException();
    }

    final String customerId = this.createCustomer(userUuid);
    this.createFreeProduct(customerId);
  }

  public @NotNull String createCustomer(final @NotNull UUID userUuid) throws StripeException {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Map<String, Object> params =
        Map.of("id", UUID.randomUUID(), "name", user.getUsername(), "email", user.getEmail());

    final Customer customer = Customer.create(params);
    user.setStripeCustomerId(UUID.fromString(customer.getId()));

    this.userRepository.save(user);
    return customer.getId();
  }

  private void createFreeProduct(final @NotNull String customerId) throws StripeException {
    if (!this.enableStripe) {
      return;
    }

    final Map<String, String> price = Map.of("price", this.stripeFreeProductPriceId);
    final List<Object> items = List.of(price);
    final Map<String, Object> params = Map.of("customer", customerId, "items", items);

    Subscription.create(params);
  }
}
