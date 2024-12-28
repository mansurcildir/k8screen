package io.k8screen.backend.controller;

import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final @NotNull UserService userService;

  public UserController(final @NotNull UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/profile")
  public ResponseEntity<UserItem> getProfile(final @NotNull Authentication authentication) {
    final var username = authentication.getName();
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.findByUsername(username));
  }
}
