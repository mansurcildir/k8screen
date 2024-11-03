package io.k8screen.backend.config;

import io.k8screen.backend.mapper.ServiceConverter;
import io.kubernetes.client.Exec;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

  @Bean
  public CoreV1Api coreV1Api() throws IOException {
    final ApiClient client = Config.defaultClient();
    return new CoreV1Api(client);
  }

  @Bean
  public AppsV1Api appsV1Api() throws IOException {
    final ApiClient client = Config.defaultClient();
    return new AppsV1Api(client);
  }

  @Bean
  public PodLogs podLogs() throws IOException {
    final ApiClient client = Config.defaultClient();
    return new PodLogs(client);
  }

  @Bean
  public Exec exec() throws IOException {
    final ApiClient client = Config.defaultClient();
    return new Exec(client);
  }
}
