package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.user.AuthResponse;
import io.k8screen.backend.data.dto.user.GoogleToken;
import io.k8screen.backend.data.dto.user.GoogleUserInfo;
import io.k8screen.backend.data.dto.user.UserGoogleLogin;
import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.data.dto.user.UserLogin;
import io.k8screen.backend.data.dto.user.UserRegister;
import io.k8screen.backend.data.entity.RefreshToken;
import io.k8screen.backend.data.entity.Role;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.UnauthorizedException;
import io.k8screen.backend.mapper.UserConverter;
import io.k8screen.backend.repository.RefreshTokenRepository;
import io.k8screen.backend.repository.RoleRepository;
import io.k8screen.backend.repository.UserRepository;
import io.k8screen.backend.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;

  @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
  private String authorizationGrantType;

  @Value("${spring.security.oauth2.client.provider.google.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
  private String userInfoUri;

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull PasswordEncoder passwordEncoder;
  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull RefreshTokenRepository refreshTokenRepository;
  private final @NotNull UserConverter userConverter;

  public @NotNull AuthResponse login(final @NotNull UserLogin loginRequest) {
    final User user =
        this.userRepository
            .findByUsernameAndDeletedFalse(loginRequest.username())
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final boolean matched =
        this.passwordEncoder.matches(loginRequest.password(), user.getPassword());

    if (user.getPassword() == null) {
      throw new UnauthorizedException("accessDenied");
    }

    if (!matched) {
      throw new UnauthorizedException("wrongPassword");
    }

    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();
    final String accessToken =
        this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    this.logout(user.getUuid());
    this.createRefreshToken(user, refreshToken);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public @NotNull AuthResponse loginGoogle(final @NotNull UserGoogleLogin loginRequest) {
    final GoogleToken googleToken = this.getGoogleTokens(loginRequest.code());
    final String token = googleToken.accessToken();
    final GoogleUserInfo userInfo = this.getGoogleUserInfo(token);
    final String email = userInfo.email();
    final String picture = userInfo.picture();
    final String username = email.split("@")[0];

    final User user =
        this.userRepository
            .findByUsernameAndDeletedFalse(username)
            .orElseGet(() -> this.createUser(username, email, picture));

    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();
    final String accessToken =
        this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    this.logout(user.getUuid());
    this.createRefreshToken(user, refreshToken);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  private @NotNull User createUser(
      final @NotNull String username, final @NotNull String email, final @NotNull String picture) {
    final User user =
        User.builder()
            .uuid(UUID.randomUUID())
            .username(username)
            .email(email)
            .picture(picture)
            .build();

    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));

    user.setRoles(Set.of(role));
    return this.userRepository.save(user);
  }

  public @NotNull AuthResponse register(final @NotNull UserRegister userRegister) {
    final String encodedPassword = this.passwordEncoder.encode(userRegister.password());
    final User user =
        this.userConverter.toUser(
            UserRegister.builder()
                .username(userRegister.username())
                .password(encodedPassword)
                .email(userRegister.email())
                .picture(userRegister.picture())
                .build());
    user.setUuid(UUID.randomUUID());

    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));
    user.setRoles(Set.of(role));

    this.userRepository.save(user);

    log.warn("User: {} registered to auth-service!", user.getUuid());

    final String accessToken =
        this.jwtUtil.generateAccessToken(
            user.getUuid(), user.getUsername(), List.of(role.getName()));

    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    this.createRefreshToken(user, refreshToken);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public @NotNull AuthResponse refresh(final @NotNull String token) {
    final UUID userUuid = this.jwtUtil.getUserUuidFromRefreshToken(token);
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();

    final Optional<RefreshToken> refreshToken = this.refreshTokenRepository.findByToken(token);

    if (refreshToken.isPresent()) {
      final String accessToken =
          this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
      final String newRefreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

      return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
    }

    throw new UnauthorizedException("accessDenied");
  }

  public void logout(final @NotNull UUID userUuid) {
    this.refreshTokenRepository.deleteAllByUserUuid(userUuid);
  }

  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }

  private void createRefreshToken(final @NotNull User user, final @NotNull String token) {
    final RefreshToken refreshToken =
        RefreshToken.builder().uuid(UUID.randomUUID()).token(token).user(user).build();

    this.refreshTokenRepository.save(refreshToken);
  }

  private @NotNull GoogleToken getGoogleTokens(final @NotNull String code) {
    final RestTemplate restTemplate = new RestTemplate();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    final String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

    final MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("code", decodedCode);
    data.add("client_id", this.clientId);
    data.add("client_secret", this.clientSecret);
    data.add("redirect_uri", this.redirectUri);
    data.add("grant_type", this.authorizationGrantType);

    final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);
    final ResponseEntity<GoogleToken> response =
        restTemplate.exchange(this.tokenUri, HttpMethod.POST, entity, GoogleToken.class);

    return Objects.requireNonNull(response.getBody());
  }

  private @NotNull GoogleUserInfo getGoogleUserInfo(final @NotNull String token) {
    final RestTemplate restTemplate = new RestTemplate();

    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

    final ResponseEntity<GoogleUserInfo> response =
        restTemplate.exchange(this.userInfoUri, HttpMethod.GET, entity, GoogleUserInfo.class);

    return Objects.requireNonNull(response.getBody());
  }
}
