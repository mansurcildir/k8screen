package io.k8screen.backend.integration.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.k8s.config.Config;
import io.k8screen.backend.k8s.config.ConfigRepository;
import io.k8screen.backend.k8s.config.ConfigService;
import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import io.k8screen.backend.subscription.SubscriptionPlan;
import io.k8screen.backend.subscription.SubscriptionPlanRepository;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ConfigServiceIT {

  private static final @NotNull String SUBSCRIPTION_PLAN_FREE = "Free";

  @Autowired private Flyway flyway;
  @Autowired private ConfigService configService;
  @Autowired private UserRepository userRepository;
  @Autowired private SubscriptionPlanRepository subscriptionPlanRepository;
  @Autowired private ConfigRepository configRepository;

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

  @Test
  public void test_createConfig_void() throws Exception {
    final User user = this.createUser();

    final FileSystemResource resource = new FileSystemResource("src/test/resources/test-config");
    byte[] content = resource.getInputStream().readAllBytes();

    final MockPart filePart = new MockPart("config", "test-config", content);

    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.addPart(filePart);

    this.configService.createConfig(request, user.getUuid());

    final Optional<Config> saved =
        configRepository.findByNameAndUserUuid("test-config", user.getUuid());
    Assertions.assertTrue(saved.isPresent());
  }

  @Test
  public void test_findAllConfigs_ListOfConfigInfo() {
    final User user = this.createUser();

    final List<ConfigInfo> configs = this.configService.findAllConfigs(user.getUuid());

    Assertions.assertFalse(configs.isEmpty());
  }
}
