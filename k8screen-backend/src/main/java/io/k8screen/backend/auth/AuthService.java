package io.k8screen.backend.auth;

import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.exception.UnauthorizedException;
import io.k8screen.backend.refresh_token.RefreshToken;
import io.k8screen.backend.refresh_token.RefreshTokenRepository;
import io.k8screen.backend.stripe.StripeService;
import io.k8screen.backend.subscription.SubscriptionPlan;
import io.k8screen.backend.subscription.SubscriptionPlanRepository;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserConverter;
import io.k8screen.backend.user.UserRepository;
import io.k8screen.backend.user.dto.AuthResponse;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.user.dto.UserLogin;
import io.k8screen.backend.user.dto.UserRegister;
import io.k8screen.backend.user.role.Role;
import io.k8screen.backend.user.role.RoleRepository;
import io.k8screen.backend.util.JwtUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  private static final @NotNull String SUBSCRIPTION_PLAN_FREE = "Free";

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull PasswordEncoder passwordEncoder;
  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull RefreshTokenRepository refreshTokenRepository;
  private final @NotNull SubscriptionPlanRepository subscriptionPlanRepository;
  private final @NotNull StripeService stripeService;
  private final @NotNull UserConverter userConverter;

  public @NotNull AuthResponse login(final @NotNull UserLogin loginRequest) {
    final User user =
        this.userRepository
            .findByUsername(loginRequest.username())
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final boolean matched =
        this.passwordEncoder.matches(loginRequest.password(), user.getPassword());

    if (user.getPassword() == null) {
      throw new UnauthorizedException("accessDenied");
    }

    if (!matched) {
      throw new UnauthorizedException("wrongPassword");
    }

    this.logout(user.getUuid());
    return this.createAuthResponse(user);
  }

  public @NotNull AuthResponse createAuthResponse(final @NotNull User user) {
    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();

    final String accessToken =
        this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    final RefreshToken token =
        RefreshToken.builder().uuid(UUID.randomUUID()).token(refreshToken).user(user).build();

    this.refreshTokenRepository.save(token);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @SneakyThrows
  public @NotNull AuthResponse register(final @NotNull UserRegister userRegister) {
    String encodedPassword = null;
    if (userRegister.password() != null) {
      encodedPassword = this.passwordEncoder.encode(userRegister.password());
    }

    final User user =
        this.createUser(
            userRegister.username(),
            userRegister.email(),
            encodedPassword,
            userRegister.avatarUrl());

    log.warn("{} has been registered!", user.getEmail());

    final String customerId = this.stripeService.createStripeCustomer(user);
    if (customerId != null) {
      user.setStripeCustomerId(UUID.fromString(customerId));
      this.userRepository.save(user);
    }

    return this.createAuthResponse(user);
  }

  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }

  public @NotNull AuthResponse refresh(final @NotNull String token) {
    final UUID userUuid = this.jwtUtil.getUserUuidFromRefreshToken(token);
    final User user =
        this.userRepository
            .findByUuid(userUuid)
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

  @CacheEvict(value = "users", key = "#userUuid")
  public void logout(final @NotNull UUID userUuid) {
    this.refreshTokenRepository.deleteAllByUserUuid(userUuid);
    this.refreshTokenRepository.flush();
  }

  public @NotNull User createUser(
      final @NotNull String username,
      final @NotNull String email,
      final @Nullable String password,
      final @Nullable String picture) {
    final User user =
        User.builder()
            .uuid(UUID.randomUUID())
            .username(username)
            .email(email)
            .password(password)
            .avatarUrl(picture)
            .build();

    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));

    user.setRoles(new HashSet<>(Set.of(role)));

    final SubscriptionPlan subscriptionPlan =
        this.subscriptionPlanRepository
            .findByName(SUBSCRIPTION_PLAN_FREE)
            .orElseThrow(() -> new ItemNotFoundException("subscriptionPlanNotFound"));

    user.setSubscriptionPlan(subscriptionPlan);
    return this.userRepository.save(user);
  }
}
