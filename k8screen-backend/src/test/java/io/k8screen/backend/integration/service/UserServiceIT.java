package io.k8screen.backend.integration.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.k8s.config.Config;
import io.k8screen.backend.k8s.config.ConfigRepository;
import io.k8screen.backend.k8s.config.dto.UserConfig;
import io.k8screen.backend.subscription.SubscriptionPlan;
import io.k8screen.backend.subscription.SubscriptionPlanRepository;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserRepository;
import io.k8screen.backend.user.UserService;
import io.k8screen.backend.user.dto.UserInfo;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserServiceIT {

  private static final @NotNull String SUBSCRIPTION_PLAN_FREE = "Free";

  @Autowired private Flyway flyway;
  @Autowired private UserRepository userRepository;
  @Autowired private SubscriptionPlanRepository subscriptionPlanRepository;
  @Autowired private ConfigRepository configRepository;
  @Autowired private UserService userService;

  @BeforeEach
  void resetDatabase() {
    flyway.clean();
    flyway.migrate();
  }

  private @NotNull User createUser() {
    final SubscriptionPlan subscriptionPlan =
        this.subscriptionPlanRepository
            .findByName(SUBSCRIPTION_PLAN_FREE)
            .orElseThrow(() -> new ItemNotFoundException("subscriptionPlanNotFound"));

    final User user =
        User.builder()
            .uuid(UUID.randomUUID())
            .username("test")
            .email("test@gamil.com")
            .password("test-password")
            .subscriptionPlan(subscriptionPlan)
            .build();
    return userRepository.save(user);
  }

  private void createConfigInstance(final @NotNull User user, final @NotNull String name) {
    final Config config = Config.builder().uuid(UUID.randomUUID()).name(name).user(user).build();

    this.configRepository.save(config);
  }

  @Test
  public void test_getUserInfo_returnUserInfo() {
    final User user = this.createUser();
    final UserInfo response = this.userService.getUserInfo(user.getUuid());

    Assertions.assertNotNull(response);
    Assertions.assertEquals("test", response.username());
  }

  @Test
  public void test_updateActiveConfig_returnUserInfo() {
    final User user = this.createUser();
    this.createConfigInstance(user, "config");

    final UserConfig userConfig = UserConfig.builder().config("config").build();
    this.userService.changeActiveConfig(userConfig, user.getUuid());

    User response = userRepository.findById(user.getId()).orElseThrow();

    Assertions.assertNotNull(response);
    Assertions.assertEquals("test", response.getActiveConfig());
  }
}
