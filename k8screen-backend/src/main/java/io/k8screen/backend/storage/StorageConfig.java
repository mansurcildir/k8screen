package io.k8screen.backend.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
  private static final String FILE_SYSTEM = "file-system";

  @Value("${k8screen.storage.strategy}")
  private String storageType;

  @Bean
  public StorageStrategy storageStrategy() {
    if (FILE_SYSTEM.equals(this.storageType)) {
      return new FileSystem();
    }

    return new S3();
  }
}
