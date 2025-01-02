package io.k8screen.backend.controller;

import io.k8screen.backend.config.CustomUserDetails;
import io.k8screen.backend.data.config.ConfigForm;
import io.k8screen.backend.data.config.ConfigItem;
import io.k8screen.backend.service.ConfigService;
import io.k8screen.backend.service.FileSystemService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/configs")
public class ConfigController {

  private final ConfigService configService;
  private final @NotNull FileSystemService fileSystemService;

  public ConfigController(
      final @NotNull ConfigService configService,
      final @NotNull FileSystemService fileSystemService) {
    this.configService = configService;
    this.fileSystemService = fileSystemService;
  }

  @GetMapping({"", "/"})
  public ResponseEntity<List<ConfigItem>> getAllConfigs(
      final @NotNull Authentication authentication) {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final List<ConfigItem> result = this.configService.findAllConfigs(userId);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping({"/{name}", "/{name}/"})
  public ResponseEntity<ConfigItem> getConfig(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String name) {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final ConfigItem result = this.configService.findConfigByName(name, userId);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @PostMapping({"", "/"})
  public ResponseEntity<Boolean> createConfig(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull ConfigForm configForm) {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    this.configService.createConfig(configForm, userId);

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }

  @PostMapping({"/upload", "/upload/"})
  public ResponseEntity<Boolean> uploadConfigFile(
      final @NotNull Authentication authentication,
      @RequestParam("config") final MultipartFile file)
      throws IOException {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    final ConfigForm configForm = ConfigForm.builder().name(file.getOriginalFilename()).build();
    this.fileSystemService.uploadConfig(file, userId);
    this.configService.createConfig(configForm, userId);

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }
}
