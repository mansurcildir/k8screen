package io.k8screen.backend.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.k8screen.backend.auth.AuthService;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.dto.AuthResponse;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.user.dto.UserLogin;
import io.k8screen.backend.user.dto.UserRegister;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthServiceIT {

  @Autowired private Flyway flyway;

  @Autowired private AuthService authService;

  @BeforeEach
  void resetDatabase() {
    flyway.clean();
    flyway.migrate();
  }

  private @NotNull AuthResponse register() {
    final UserRegister userRegister = new UserRegister("test", "Tester123", "test@gmail.com", null);
    return this.authService.register(userRegister);
  }

  @Test
  public void test_login_returnAuthResponse() {
    this.register();

    final UserLogin userLogin = new UserLogin("test", "Tester123");
    final AuthResponse response = this.authService.login(userLogin);

    assertThat(response).isNotNull();
    assertThat(response.accessToken()).isNotNull();
  }

  @Test
  public void test_register_returnAuthResponse() {
    final UserRegister userRegister = new UserRegister("test", "Tester123", "test@gmail.com", null);
    final AuthResponse response = this.authService.register(userRegister);

    assertThat(response).isNotNull();
    assertThat(response.accessToken()).isNotNull();
  }

  @Test
  public void test_createUser_returnUserInfo() {
    final User response = this.authService.createUser("test", "test@gmail.com", "Tester123", null);

    assertThat(response).isNotNull();
    assertThat(response.getUsername()).isEqualTo("test");
  }

  @Test
  public void test_getUserInfo_returnUserInfo() {
    final User user = this.authService.createUser("test", "test@gmail.com", "Tester123", null);
    final UserInfo response = this.authService.getUserInfo(user.getUuid());

    assertThat(response).isNotNull();
    assertThat(response.username()).isEqualTo("test");
  }

  @Test
  public void test_refresh_returnAuthResponse() {
    final AuthResponse authResponse = this.register();
    final AuthResponse response = this.authService.refresh(authResponse.refreshToken());

    assertThat(response).isNotNull();
    assertThat(response.accessToken()).isNotNull();
  }
}
