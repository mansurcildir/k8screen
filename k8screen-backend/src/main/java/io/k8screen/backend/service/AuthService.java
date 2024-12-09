package io.k8screen.backend.service;

import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.data.user.UserLoginRequest;
import io.k8screen.backend.util.JwtUtil;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final @NotNull AuthenticationManager authManager;
  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull PasswordEncoder passwordEncoder;
  private final @NotNull UserService userService;

  public AuthService(
      final @NotNull AuthenticationManager authManager,
      final @NotNull JwtUtil jwtUtil,
      final @NotNull PasswordEncoder passwordEncoder,
      final @NotNull UserService userService) {
    this.authManager = authManager;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  public Map<String, String> login(final @NotNull UserLoginRequest loginRequest) {
    final Authentication authentication =
        this.authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    if (authentication.isAuthenticated()) {
      final String accessToken = this.jwtUtil.generateAccessToken(loginRequest.getUsername());
      final String refreshToken = this.jwtUtil.generateRefreshToken(loginRequest.getUsername());

      return Map.of(
          "accessToken", accessToken,
          "refreshToken", refreshToken);

    } else {
      throw new BadCredentialsException("Username or password is invalid");
    }
  }

  public Map<String, String> register(final @NotNull UserForm userForm) {
    final String encoded = this.passwordEncoder.encode(userForm.getPassword());
    userForm.setPassword(encoded);
    final UserItem createdUser = this.userService.create(userForm);

    final String accessToken = this.jwtUtil.generateAccessToken(createdUser.username());
    final String refreshToken = this.jwtUtil.generateAccessToken(createdUser.username());

    return Map.of(
        "accessToken", accessToken,
        "refreshToken", refreshToken);
  }

  public Map<String, String> getAccessToken(final @NotNull String refreshToken) {
    final String username = this.jwtUtil.getRefreshClaim(refreshToken);
    final String accessToken = this.jwtUtil.generateAccessToken(username);
    return Map.of("accessToken", accessToken);
  }
}
