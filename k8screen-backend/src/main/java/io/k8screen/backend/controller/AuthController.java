package io.k8screen.backend.controller;

import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserLoginReq;
import io.k8screen.backend.service.AuthService;
import io.k8screen.backend.service.UserService;
import io.k8screen.backend.util.JwtUtil;
import jakarta.validation.Valid;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

  /*
  @PostMapping("/google/tokens")
  public ResponseEntity<String> loginGoogle(final @RequestBody @NotNull GoogleCredentials credentials) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<GoogleCredentials> entity = new HttpEntity<>(credentials, headers);

    final var response = restTemplate.exchange("https://oauth2.googleapis.com/token", HttpMethod.POST, entity, Object.class);
    final var body = response.getBody();

    return ResponseEntity.status(HttpStatus.OK).body("");

  }
  */

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
