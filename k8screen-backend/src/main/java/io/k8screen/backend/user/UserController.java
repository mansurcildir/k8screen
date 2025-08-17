package io.k8screen.backend.user;

import io.k8screen.backend.k8s.config.dto.UserConfig;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.UserDetails;
import io.k8screen.backend.user.dto.UserInfo;
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
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final @NotNull UserService userService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping("/profile")
  public @NotNull ResponseEntity<Result> getProfile(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final UserInfo userInfo = this.userService.getUserInfo(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "profileFetched", userInfo));
  }

  @PutMapping("/config")
  public @NotNull ResponseEntity<Result> changeActiveConfig(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull UserConfig userConfig) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.userService.changeActiveConfig(userConfig, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "configUpdated"));
  }
}
