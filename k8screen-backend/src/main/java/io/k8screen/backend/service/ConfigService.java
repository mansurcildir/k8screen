package io.k8screen.backend.service;

import com.github.f4b6a3.ulid.UlidCreator;
import io.k8screen.backend.core.exception.ItemNotFoundException;
import io.k8screen.backend.data.dto.config.ConfigInfo;
import io.k8screen.backend.data.entity.Config;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.mapper.ConfigConverter;
import io.k8screen.backend.repository.ConfigRepository;
import io.k8screen.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull ConfigConverter configConverter;
  private final @NotNull FileSystemService fileSystemService;

  public List<ConfigInfo> findAllConfigs(final @NotNull String userId) {
    final List<Config> configs = this.configRepository.findAllByUserId(userId);
    return configs.stream().map(this.configConverter::toConfigItem).toList();
  }

  public ConfigInfo findConfigByName(final @NotNull String name, final @NotNull String userId) {
    final Config config =
        this.configRepository
            .findByNameAndUserId(name, userId)
            .orElseThrow(() -> new ItemNotFoundException("Config not found"));
    return ConfigInfo.builder().id(config.getId()).name(config.getName()).build();
  }

  public void createConfig(final @NotNull MultipartFile file, final @NotNull String userId)
      throws IOException {
    final User user =
        this.userRepository
            .findById(userId)
            .orElseThrow(() -> new ItemNotFoundException("User not found"));

    final Config config = Config.builder().name(file.getOriginalFilename()).build();
    config.setId(UlidCreator.getUlid().toString());
    config.setUser(user);
    final Config createdConfig = this.configRepository.save(config);

    this.configConverter.toConfigItem(createdConfig);
    this.fileSystemService.uploadConfig(file, userId);
  }

  public void deleteConfigByName(final @NotNull String name, final @NotNull String userId)
      throws IOException {
    this.configRepository
        .deleteByName(name)
        .orElseThrow(() -> new ItemNotFoundException("Config not found"));
    this.fileSystemService.deleteConfig(name, userId);
  }
}
