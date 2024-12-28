package io.k8screen.backend.controller;

import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserLoginReq;
import io.k8screen.backend.service.AuthService;
import io.k8screen.backend.service.UserService;
import io.k8screen.backend.util.JwtUtil;
import jakarta.validation.Valid;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;

  private final @NotNull AuthService authService;
  private final @NotNull UserService userService;
  private final @NotNull JwtUtil jwtUtil;

  public AuthController(
      final @NotNull AuthService authService,
      final @NotNull JwtUtil jwtUtil,
      final @NotNull UserService userService) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(
      @Valid @RequestBody final UserLoginReq loginRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(loginRequest));
  }

  @PostMapping("/login/google")
  public ResponseEntity<Object> loginGoogle(
    final @RequestBody @NotNull Map<String, String> body) {

    try {
      Map<String, Object> tokens = this.authService.loginGoogle(body.get("code"));
      return ResponseEntity.ok(tokens);
    } catch (HttpClientErrorException e) {
      return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Map.class));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody final UserForm userForm) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.authService.register(userForm));
  }

  @GetMapping("/access-token")
  public ResponseEntity<Map<String, String>> getAccessToken(
      @RequestHeader("Refresh-Token") final @NotNull String header) {
    return ResponseEntity.status(HttpStatus.OK).body(this.authService.getAccessToken(header));
  }
}
