package io.k8screen.backend.k8s.config;

import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.SubscriptionLimitExceed;
import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import io.k8screen.backend.storage.FileSystemService;
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
import org.apache.coyote.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull ConfigConverter configConverter;
  private final @NotNull FileSystemService fileSystemService;

  public @NotNull List<ConfigInfo> findAllConfigs(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<Config> configs = this.configRepository.findAllByUserId(user.getId());
    return configs.stream().map(this.configConverter::toConfigItem).toList();
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

    if (this.configCountExceedLimit(user.getSubscriptionPlan(), userUuid)) {
      throw new SubscriptionLimitExceed("subscriptionLimitExceed");
    }

    final Part filePart = request.getPart("config");
    final String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

    final Optional<Config> configOpt =
        this.configRepository.findByNameAndUserUuid(fileName, userUuid);

    if (configOpt.isPresent()) {
      throw new BadRequestException("configExists");
    }

    this.fileSystemService.uploadConfig(filePart.getInputStream(), fileName, userUuid);

    final Config config = Config.builder().name(fileName).build();
    config.setUuid(UUID.randomUUID());
    config.setUser(user);
    this.configRepository.save(config);
  }

  public void deleteConfigByName(final @NotNull String name, final @NotNull UUID userUuid)
      throws IOException {
    this.fileSystemService.deleteConfig(name, userUuid);

    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    user.setActiveConfig(null);
    this.userRepository.save(user);

    this.configRepository.deleteByNameAndUserUuid(name, userUuid);
  }

  private boolean configCountExceedLimit(
      final @NotNull SubscriptionPlan subscriptionPlan, final @NotNull UUID userUuid) {
    final long configCount = this.configRepository.countConfigByUserUuid(userUuid);

    return configCount >= subscriptionPlan.getMaxConfigCount();
  }
}
