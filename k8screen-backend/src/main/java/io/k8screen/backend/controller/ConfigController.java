package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.config.ConfigInfo;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.service.ConfigService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasRole('USER')")
  @GetMapping({"", "/"})
  public ResponseEntity<List<ConfigInfo>> getAllConfigs(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<ConfigInfo> result = this.configService.findAllConfigs(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping({"/{name}", "/{name}/"})
  public ResponseEntity<ConfigInfo> getConfig(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String name) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final ConfigInfo result = this.configService.findConfigByName(name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @PostMapping({"/upload", "/upload/"})
  public ResponseEntity<Boolean> uploadConfigFile(
      final @NotNull Authentication authentication,
      @RequestParam("config") final @NotNull MultipartFile file)
      throws IOException {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.configService.checkConfig(file, userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }

  @DeleteMapping({"/delete", "/delete/"})
  public ResponseEntity<Boolean> deleteConfigFile(
      final @NotNull Authentication authentication, @RequestParam final @NotNull String name)
      throws IOException {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.configService.deleteConfigByName(name, userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK).body(true);
  }
}
