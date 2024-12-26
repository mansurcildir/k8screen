package io.k8screen.backend.controller;

import io.k8screen.backend.data.user.GoogleCredentials;
import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserLoginReq;
import io.k8screen.backend.service.AuthService;
import io.k8screen.backend.service.UserService;
import io.k8screen.backend.util.JwtUtil;
import jakarta.validation.Valid;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

  @PostMapping("/google/tokens")
  public ResponseEntity <Object> loginGoogle(final @RequestBody @NotNull GoogleCredentials credentials) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String decodedCode = URLDecoder.decode(credentials.getCode(), StandardCharsets.UTF_8);

    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("code", decodedCode);
    data.add("client_id", credentials.getClientId());
    data.add("client_secret", credentials.getClientSecret());
    data.add("redirect_uri", credentials.getRedirectUri());
    data.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);

    try {
      ResponseEntity<Object> response = restTemplate.exchange(
        "https://oauth2.googleapis.com/token",
        HttpMethod.POST,
        entity,
        Object.class
      );
      return ResponseEntity.ok(response.getBody());
    } catch (HttpClientErrorException e) {
      return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Object.class));
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
