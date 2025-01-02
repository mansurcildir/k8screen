package io.k8screen.backend.controller;

import io.k8screen.backend.config.CustomUserDetails;
import io.k8screen.backend.data.user.UserConfig;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.service.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final @NotNull UserService userService;

  public UserController(final @NotNull UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/profile")
  public ResponseEntity<UserItem> getProfile(final @NotNull Authentication authentication) {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.findById(userId));
  }

  @PutMapping({"/config", "/config/"})
  public ResponseEntity<Boolean> updateConfig(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull UserConfig userConfig) {
    final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    final String userId = userDetails.getUserId();
    this.userService.updateConfig(userConfig, userId);
    return ResponseEntity.status(HttpStatus.OK).body(true);
  }
}
