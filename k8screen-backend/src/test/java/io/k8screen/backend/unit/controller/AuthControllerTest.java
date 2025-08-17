package io.k8screen.backend.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import io.k8screen.backend.auth.AuthController;
import io.k8screen.backend.auth.AuthService;
import io.k8screen.backend.config.TemporaryTokenStore;
import io.k8screen.backend.result.DataResult;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.AuthResponse;
import io.k8screen.backend.user.dto.UserDetails;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.user.dto.UserLogin;
import io.k8screen.backend.user.dto.UserRegister;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  private @Mock Authentication authentication;
  private @Mock AuthService authService;
  private @Mock ResponseFactory responseFactory;
  private @Mock TemporaryTokenStore tokenStore;

  private @InjectMocks AuthController authController;

  private final @NotNull AuthResponse authResponse =
      AuthResponse.builder().accessToken("test-access").refreshToken("test-refresh").build();

  final UserInfo userInfo =
      UserInfo.builder()
          .uuid(UUID.randomUUID())
          .username("test-username")
          .email("test@gmail.com")
          .roles(List.of())
          .build();

  @Test
  public void test_register_returnDataResult() {
    final UserRegister userRegister = new UserRegister("test", "Tester123", "test@gmail.com", null);

    when(this.authService.register(Mockito.any(UserRegister.class))).thenReturn(this.authResponse);

    when(this.responseFactory.success(201, "registered", this.authResponse))
        .thenReturn(Mockito.mock(DataResult.class));

    final ResponseEntity<Result> response = this.authController.register(userRegister);

    assertNotNull(response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  public void test_login_returnDataResult() {
    final UserLogin userLogin = new UserLogin("test", "Tester123");

    when(this.authService.login(Mockito.any(UserLogin.class))).thenReturn(this.authResponse);

    when(this.responseFactory.success(200, "loggedIn", this.authResponse))
        .thenReturn(Mockito.mock(DataResult.class));

    final ResponseEntity<Result> response = this.authController.login(userLogin);

    assertNotNull(response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void get_profile_returnDataResult() {
    when(this.authentication.getPrincipal())
        .thenReturn(UserDetails.builder().userUuid(UUID.randomUUID()).username("test").build());

    when(this.authService.getUserInfo(Mockito.any(UUID.class))).thenReturn(this.userInfo);

    when(this.responseFactory.success(200, "profileFetched", this.userInfo))
        .thenReturn(Mockito.mock(DataResult.class));

    final ResponseEntity<Result> response = this.authController.profile(this.authentication);

    assertNotNull(response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void get_refresh_returnDataResult() {
    when(this.authService.refresh(Mockito.any(String.class))).thenReturn(this.authResponse);

    when(this.responseFactory.success(200, "refreshed", this.authResponse))
        .thenReturn(Mockito.mock(DataResult.class));

    final ResponseEntity<Result> response = this.authController.refresh(Mockito.anyString());

    assertNotNull(response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
