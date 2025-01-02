package io.k8screen.backend.service;

import com.github.f4b6a3.ulid.UlidCreator;
import io.k8screen.backend.data.config.ConfigForm;
import io.k8screen.backend.data.config.ConfigItem;
import io.k8screen.backend.data.entity.Config;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.mapper.ConfigConverter;
import io.k8screen.backend.repository.ConfigRepository;
import io.k8screen.backend.repository.UserRepository;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

  private final UserRepository userRepository;
  private final ConfigRepository configRepository;
  private final ConfigConverter configConverter;

  public ConfigService(
      final @NotNull UserRepository userRepository,
      final @NotNull ConfigRepository configRepository,
      final @NotNull ConfigConverter configConverter) {
    this.userRepository = userRepository;
    this.configRepository = configRepository;
    this.configConverter = configConverter;
  }

  public List<ConfigItem> findAllConfigs(final @NotNull String userId) {
    final List<Config> configs = this.configRepository.findAllByUserId(userId);
    return configs.stream().map(this.configConverter::toConfigItem).toList();
  }

  public ConfigItem findConfigByName(final @NotNull String name, final @NotNull String userId) {
    final Config config = this.configRepository.findByNameAndUserId(name, userId).orElseThrow();
    return ConfigItem.builder().id(config.getId()).name(config.getName()).build();
  }

  public void createConfig(final @NotNull ConfigForm configForm, final @NotNull String userId) {
    final User user = this.userRepository.findById(userId).orElseThrow();

    final Config config = this.configConverter.toConfig(configForm);
    config.setId(UlidCreator.getUlid().toString());
    config.setUser(user);
    final Config createdConfig = this.configRepository.save(config);

    this.configConverter.toConfigItem(createdConfig);
  }
}
