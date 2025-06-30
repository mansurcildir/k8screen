package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.config.ConfigInfo;
import io.k8screen.backend.data.entity.Config;
import io.k8screen.backend.data.entity.SubscriptionPlan;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.SubscriptionLimitExceed;
import io.k8screen.backend.mapper.ConfigConverter;
import io.k8screen.backend.repository.ConfigRepository;
import io.k8screen.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<Config> configs = this.configRepository.findAllByUserIdAndDeletedFalse(user.getId());
    return configs.stream().map(this.configConverter::toConfigItem).toList();
  }

  public @NotNull ConfigInfo findConfigByName(
      final @NotNull String name, final @NotNull UUID userUuid) {

    final Config config =
        this.configRepository
            .findByNameAndUserUuidAndDeletedFalse(name, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("configNotFound"));

    return ConfigInfo.builder().name(config.getName()).build();
  }

  public void createConfig(final @NotNull HttpServletRequest request, final @NotNull UUID userUuid)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    if (this.configCountExceedLimit(user.getSubscriptionPlan(), userUuid)) {
      throw new SubscriptionLimitExceed("subscriptionLimitExceed");
    }

    final Part filePart = request.getPart("config");
    final String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

    final Optional<Config> configOpt =
        this.configRepository.findByNameAndUserUuidAndDeletedFalse(fileName, userUuid);

    if (configOpt.isPresent()) {
      throw new BadRequestException("configExists");
    }

    this.fileSystemService.uploadConfig(filePart.getInputStream(), fileName, userUuid);

    final Config config = Config.builder().name(fileName).build();
    config.setUuid(UUID.randomUUID());
    config.setUser(user);
    this.configRepository.save(config);
  }

  private boolean configCountExceedLimit(
      final @NotNull SubscriptionPlan subscriptionPlan, final @NotNull UUID userUuid) {
    final long configCount = this.configRepository.countConfigByUserUuidAndDeletedFalse(userUuid);

    return configCount >= subscriptionPlan.getMaxConfigCount();
  }

  public void deleteConfigByName(final @NotNull String name, final @NotNull UUID userUuid)
      throws IOException {
    this.fileSystemService.deleteConfig(name, userUuid);

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    user.setActiveConfig(null);
    this.userRepository.save(user);

    final Config config =
        this.configRepository
            .findByNameAndUserUuidAndDeletedFalse(name, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("configNotFound"));

    config.setDeleted(true);
    this.configRepository.save(config);
  }
}
