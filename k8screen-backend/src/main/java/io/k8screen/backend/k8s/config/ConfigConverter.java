package io.k8screen.backend.k8s.config;

import io.k8screen.backend.k8s.config.dto.ConfigForm;
import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigConverter {
  Config toConfig(ConfigForm configForm);

  ConfigInfo toConfigItem(Config config);
}
