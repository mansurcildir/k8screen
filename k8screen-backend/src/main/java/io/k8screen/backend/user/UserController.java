package io.k8screen.backend.user;

import io.k8screen.backend.k8s.config.dto.UserConfig;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.ProfileForm;
import io.k8screen.backend.user.dto.UserDetails;
import io.k8screen.backend.user.dto.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
  private final @NotNull UserStorageService userStorageService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping("/profile")
  public @NotNull ResponseEntity<Result> getProfile(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final UserInfo userInfo = this.userService.getUserInfo(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "profileFetched", userInfo));
  }

  @PutMapping("/profile")
  public @NotNull ResponseEntity<Result> updateProfile(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull ProfileForm profileForm) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.userService.updateProfile(profileForm, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "profileUpdated"));
  }

  @GetMapping("/avatar")
  public @NotNull ResponseEntity<byte[]> getAvatar(final @NotNull Authentication authentication)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final byte[] bytes = this.userStorageService.getAvatar(userDetails.userUuid());

    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
  }

  @PutMapping("/avatar")
  public @NotNull ResponseEntity<Result> uploadAvatar(
      final @NotNull Authentication authentication, final @NotNull HttpServletRequest request)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.userService.uploadAvatar(request, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "avatarUploaded"));
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
