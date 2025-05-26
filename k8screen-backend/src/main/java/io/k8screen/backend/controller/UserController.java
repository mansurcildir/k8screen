package io.k8screen.backend.controller;

import io.k8screen.backend.data.dto.config.UserConfig;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserController {

  private final @NotNull UserService userService;

  @GetMapping("/profile")
  public ResponseEntity<UserInfo> getProfile(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final UserInfo userInfo = this.userService.getUserInfo(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK).body(userInfo);
  }

  @PutMapping("/config")
  public ResponseEntity<Void> updateConfig(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull UserConfig userConfig) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.userService.updateConfig(userConfig, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
