package io.k8screen.backend.integration.controller;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.k8screen.backend.config.TemporaryTokenStore;
import io.k8screen.backend.result.DataResult;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.AuthResponse;
import io.k8screen.backend.user.dto.UserLogin;
import io.k8screen.backend.user.dto.UserRegister;
import io.k8screen.backend.util.JwtUtil;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthControllerIT {

  @Value("${jwt.access-key}")
  private String accessKey;

  @LocalServerPort private int port;

  @Autowired private Flyway flyway;

  @Autowired private JwtUtil jwtUtil;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private TemporaryTokenStore tokenStore;

  @BeforeEach
  void resetDatabase() {
    flyway.clean();
    flyway.migrate();
  }

  private @NotNull DataResult<AuthResponse> register() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final UserRegister userRegister = new UserRegister("test", "Tester123", "test@gmail.com", null);
    final HttpEntity<UserRegister> entity = new HttpEntity<>(userRegister, headers);

    final String url = "http://localhost:" + port + "/v1/auth/register";
    ResponseEntity<DataResult<AuthResponse>> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertNotNull(response.getBody());
    return response.getBody();
  }

  @Test
  public void post_register_returnDataResult() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final UserRegister userRegister = new UserRegister("test", "Tester123", "test@gmail.com", null);
    final HttpEntity<UserRegister> entity = new HttpEntity<>(userRegister, headers);

    final String url = "http://localhost:" + port + "/v1/auth/register";

    ResponseEntity<DataResult<AuthResponse>> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    Assertions.assertNotNull(response.getBody().getData().accessToken());
    Assertions.assertNotNull(
        this.jwtUtil.extractAllClaims(response.getBody().getData().accessToken(), this.accessKey));
  }

  @Test
  public void post_login_returnDataResult() {
    this.register();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final UserLogin userLogin = new UserLogin("test", "Tester123");
    final HttpEntity<UserLogin> entity = new HttpEntity<>(userLogin, headers);

    final String url = "http://localhost:" + port + "/v1/auth/login";

    ResponseEntity<DataResult<AuthResponse>> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    Assertions.assertNotNull(response.getBody().getData().accessToken());
    Assertions.assertNotNull(
        this.jwtUtil.extractAllClaims(response.getBody().getData().accessToken(), this.accessKey));
  }

  @Test
  public void get_refresh_returnDataResult() {
    final String refreshToken = this.register().getData().refreshToken();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Refresh-Token", refreshToken);

    final HttpEntity<Object> entity = new HttpEntity<>(headers);

    final String url = "http://localhost:" + port + "/v1/auth/refresh";

    ResponseEntity<DataResult<AuthResponse>> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    Assertions.assertEquals(refreshToken, response.getBody().getData().refreshToken());
  }

  @Test
  public void get_logout_returnResult() {
    final String accessToken = this.register().getData().accessToken();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);

    final HttpEntity<Object> entity = new HttpEntity<>(headers);

    final String url = "http://localhost:" + port + "/v1/auth/logout";

    ResponseEntity<Result> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, Result.class);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void get_tokenFromSession_returnDataResult() {
    final String sessionId = "test-session-id";
    final AuthResponse authResponse = new AuthResponse("test-access-token", "test-refresh-token");
    this.tokenStore.put(sessionId, authResponse);

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final HttpEntity<Void> entity = new HttpEntity<>(headers);

    final String url = "http://localhost:" + port + "/v1/auth/token/" + sessionId;

    ResponseEntity<DataResult<AuthResponse>> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    Assertions.assertEquals("test-refresh-token", response.getBody().getData().refreshToken());
  }
}
