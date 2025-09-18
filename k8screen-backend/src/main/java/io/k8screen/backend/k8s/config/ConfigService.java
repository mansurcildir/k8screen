package io.k8screen.backend.k8s.config;

import io.k8screen.backend.exception.BadRequestException;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.SubscriptionLimitExceed;
import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import io.k8screen.backend.subscription.SubscriptionPlan;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigService {
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull ConfigConverter configConverter;
  private final @NotNull ConfigStorageService configStorageService;
  private final @NotNull UserRepository userRepository;

  public @NotNull List<ConfigInfo> findAllConfigs(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<Config> configs = this.configRepository.findAllByUserId(user.getId());
    return configs.stream().map(this.configConverter::toConfigInfo).toList();
  }

  public @NotNull ConfigInfo findConfigByName(
      final @NotNull String name, final @NotNull UUID userUuid) {

    final Config config =
        this.configRepository
            .findByNameAndUserUuid(name, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("configNotFound"));

    return ConfigInfo.builder().name(config.getName()).build();
  }

  public void createConfig(final @NotNull HttpServletRequest request, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    this.checkConfigCountExceedLimit(user.getSubscriptionPlan(), userUuid);

    final Part filePart = request.getPart("config");
    final String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

    final Optional<Config> configOpt =
        this.configRepository.findByNameAndUserUuid(fileName, userUuid);

    this.checkConfigExists(configOpt.isPresent());

    this.configStorageService.uploadConfig(filePart.getInputStream(), fileName, userUuid);

    final Config config = Config.builder().name(fileName).build();
    config.setUuid(UUID.randomUUID());
    config.setUser(user);
    this.configRepository.save(config);
  }

  @CacheEvict(value = "users", key = "#userUuid")
  public void deleteConfigByName(final @NotNull String name, final @NotNull UUID userUuid)
      throws IOException {
    this.configStorageService.deleteConfig(name, userUuid);

    this.setNullActiveConfig(userUuid);
    this.configRepository.deleteByNameAndUserUuid(name, userUuid);
  }

  private void setNullActiveConfig(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    user.setActiveConfig(null);
    this.userRepository.save(user);
  }

  private void checkConfigCountExceedLimit(
      final @NotNull SubscriptionPlan subscriptionPlan, final @NotNull UUID userUuid) {
    final long configCount = this.configRepository.countConfigByUserUuid(userUuid);

    if (configCount >= subscriptionPlan.getMaxConfigCount()) {
      throw new SubscriptionLimitExceed("subscriptionLimitExceed");
    }
  }

  private void checkConfigExists(final boolean present) {
    if (present) {
      throw new BadRequestException("configExists");
    }
  }
}
