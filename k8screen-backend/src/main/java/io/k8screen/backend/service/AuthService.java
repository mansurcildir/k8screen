package io.k8screen.backend.service;

import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.data.user.UserLoginReq;
import io.k8screen.backend.util.JwtUtil;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;

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

  public Map<String, String> login(final @NotNull UserLoginReq loginRequest) {
    final Authentication authentication =
        this.authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    if (authentication.isAuthenticated()) {
      final String accessToken = this.jwtUtil.generateAccessToken(loginRequest.getUsername());
      final String refreshToken = this.jwtUtil.generateRefreshToken(loginRequest.getUsername());

      return Map.of(
          "access_token", accessToken,
          "refresh_token", refreshToken);

    } else {
      throw new BadCredentialsException("Username or password is invalid");
    }
  }

  public Map<String, Object> loginGoogle(final @NotNull String code) {
    String token = (String) this.getGoogleTokens(code).get("access_token");
    String email = (String) this.getUserInfo(token).get("email");
    String picture = (String) this.getUserInfo(token).get("picture");
    String username = email.split("@")[0];

    try {
      this.userService.findByUsername(username);
    } catch (NoSuchElementException e) {
      createUserIfNotExists(email, username, picture);
    }

    String accessToken = this.jwtUtil.generateAccessToken(username);
    String refreshToken = this.jwtUtil.generateRefreshToken(username);

    return Map.of(
      "access_token", accessToken,
      "refresh_token", refreshToken
    );
  }

  private void createUserIfNotExists(String email, String username, String picture) {
    UserForm userForm = UserForm.builder()
      .email(email)
      .username(username)
      .password("dummy")
      .picture(picture)
      .build();

    try {
      this.userService.create(userForm);
    } catch (RuntimeException e) {
      throw new RuntimeException("User creation failed", e);
    }
  }

  public Map<String, Object> getGoogleTokens(final @NotNull String code) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("code", decodedCode);
    data.add("client_id", clientId);
    data.add("client_secret", clientSecret);
    data.add("redirect_uri", redirectUri);
    data.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);

    ResponseEntity<Map> response = restTemplate.exchange(
      "https://oauth2.googleapis.com/token",
      HttpMethod.POST,
      entity,
      Map.class);

    Map<String, Object> responseBody = response.getBody();
    return responseBody;
  }

  public Map<String, Object> getUserInfo(final @NotNull String token) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
      "https://www.googleapis.com/oauth2/v1/userinfo?alt=json",
      HttpMethod.GET,
      entity,
      Map.class);

    Map<String, Object> responseBody = response.getBody();
    return responseBody;
  }

  public Map<String, String> register(final @NotNull UserForm userForm) {
    final String encoded = this.passwordEncoder.encode(userForm.getPassword());
    userForm.setPassword(encoded);
    final UserItem createdUser = this.userService.create(userForm);

    final String accessToken = this.jwtUtil.generateAccessToken(createdUser.username());
    final String refreshToken = this.jwtUtil.generateAccessToken(createdUser.username());

    return Map.of(
        "access_token", accessToken,
        "refresh_token", refreshToken);
  }

  public Map<String, String> getAccessToken(final @NotNull String refreshToken) {
    final String username = this.jwtUtil.getRefreshClaim(refreshToken);
    final String accessToken = this.jwtUtil.generateAccessToken(username);
    return Map.of("access_token", accessToken);
  }
}
