package io.k8screen.backend.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.k8screen.backend.k8s.config.dto.UserConfig;
import io.k8screen.backend.result.DataResult;
import io.k8screen.backend.result.Result;
import io.k8screen.backend.user.dto.AuthResponse;
import io.k8screen.backend.user.dto.UserInfo;
import io.k8screen.backend.user.dto.UserRegister;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerIT {
  @LocalServerPort private int port;

  @Autowired private Flyway flyway;

  @Autowired private TestRestTemplate restTemplate;

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
    final ResponseEntity<DataResult<AuthResponse>> response =
        this.restTemplate.exchange(
            url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

    Assertions.assertNotNull(response.getBody());
    return response.getBody();
  }

  public void uploadConfig(final @NotNull String accessToken) {
    final String filePath = "src/test/resources/test-config";

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.setBearerAuth(accessToken);

    final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("config", new FileSystemResource(filePath));

    final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    final String url = "http://localhost:" + port + "/v1/configs";

    this.restTemplate.exchange(
        url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});
  }

  @Test
  public void put_updateConfig_returnDataResult() {
    final String accessToken = this.register().getData().accessToken();
    this.uploadConfig(accessToken);

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);

    final UserConfig userConfig = UserConfig.builder().config("test-config").build();

    final HttpEntity<Object> entity = new HttpEntity<>(userConfig, headers);

    final String url = "http://localhost:" + port + "/v1/users/config";

    final ResponseEntity<Result> response =
        this.restTemplate.exchange(url, HttpMethod.PUT, entity, Result.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  public void get_profile_returnDataResult() {
    final String accessToken = this.register().getData().accessToken();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);

    final HttpEntity<Object> entity = new HttpEntity<>(headers);

    final String url = "http://localhost:" + port + "/v1/users/profile";

    ResponseEntity<DataResult<UserInfo>> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getData()).isNotNull();
    assertThat(response.getBody().getData().username()).isEqualTo("test");
  }
}
