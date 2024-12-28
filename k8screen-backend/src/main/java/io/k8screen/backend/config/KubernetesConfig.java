package io.k8screen.backend.config;

import io.kubernetes.client.Exec;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KubernetesConfig {

  private ApiClient apiClient() throws IOException {
    return Config.defaultClient();
  }

  @Bean
  public CoreV1Api coreV1Api() throws IOException {
    return new CoreV1Api(apiClient());
  }

  @Bean
  public AppsV1Api appsV1Api() throws IOException {
    return new AppsV1Api(apiClient());
  }

  @Bean
  public PodLogs podLogs() throws IOException {
    return new PodLogs(apiClient());
  }

  @Bean
  public Exec exec() throws IOException {
    return new Exec(apiClient());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

