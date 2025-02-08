package io.k8screen.backend.controller;

import io.k8screen.backend.config.CustomUserDetails;
import io.k8screen.backend.data.config.ConfigItem;
import io.k8screen.backend.service.ConfigService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
public class ConfigController {

  private final @NotNull ConfigService configService;

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

  @PostMapping({"/upload", "/upload/"})
  public ResponseEntity<Boolean> uploadConfigFile(
      final @NotNull Authentication authentication,
      @RequestParam("config") final @NotNull MultipartFile file)
      throws IOException {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    this.configService.createConfig(file, userId);

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }

  @DeleteMapping({"/delete", "/delete/"})
  public ResponseEntity<Boolean> deleteConfigFile(
      final @NotNull Authentication authentication, @RequestParam final @NotNull String name)
      throws IOException {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    this.configService.deleteConfigByName(name, userId);

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }
}
