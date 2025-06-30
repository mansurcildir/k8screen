package io.k8screen.backend.controller;

import static io.k8screen.backend.util.Constant.REFRESH_TOKEN;

import com.stripe.exception.StripeException;
import io.k8screen.backend.data.dto.user.AuthResponse;
import io.k8screen.backend.data.dto.user.UserDetails;
import io.k8screen.backend.data.dto.user.UserGoogleLogin;
import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.data.dto.user.UserLogin;
import io.k8screen.backend.data.dto.user.UserRegister;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final @NotNull AuthService authService;
  private final @NotNull ResponseFactory responseFactory;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody final UserLogin loginRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(loginRequest));
  }

  @PostMapping("/login/google")
  public ResponseEntity<AuthResponse> loginGoogle(
      @Valid @RequestBody final UserGoogleLogin loginRequest) {
    final AuthResponse authResponse = this.authService.loginGoogle(loginRequest);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
      @Valid @RequestBody final @NotNull UserRegister userRegister) throws StripeException {
    final AuthResponse authResponse = this.authService.register(userRegister);
    return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
  }

  @GetMapping("/logout")
  public ResponseEntity<Result> logout(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.authService.logout(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "loggedOut"));
  }

  @GetMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @RequestHeader(REFRESH_TOKEN) final @NotNull String header) {
    return ResponseEntity.status(HttpStatus.OK).body(this.authService.refresh(header));
  }

  @GetMapping("/profile")
  public ResponseEntity<UserInfo> profile(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.authService.getUserInfo(userDetails.userUuid()));
  }
}
