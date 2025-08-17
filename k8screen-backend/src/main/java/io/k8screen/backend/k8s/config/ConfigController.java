package io.k8screen.backend.k8s.config;

import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/v1/configs")
@RequiredArgsConstructor
public class ConfigController {

  private final @NotNull ConfigService configService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping
  public @NotNull ResponseEntity<Result> getAllConfigs(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<ConfigInfo> configs = this.configService.findAllConfigs(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "configsFetched", configs));
  }

  @GetMapping("/{name}")
  public @NotNull ResponseEntity<Result> getConfig(
      final @NotNull Authentication authentication, @PathVariable final @NotNull String name) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final ConfigInfo config = this.configService.findConfigByName(name, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "configFetched", config));
  }

  @PostMapping
  public @NotNull ResponseEntity<Result> uploadConfig(
      final @NotNull Authentication authentication, final @NotNull HttpServletRequest request)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.configService.createConfig(request, userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "configUploaded"));
  }

  @DeleteMapping
  public @NotNull ResponseEntity<Result> deleteConfig(
      final @NotNull Authentication authentication, @RequestParam final @NotNull String name)
      throws IOException {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.configService.deleteConfigByName(name, userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "configDeleted"));
  }
}
