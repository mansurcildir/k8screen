package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.config.ConfigForm;
import io.k8screen.backend.data.dto.config.ConfigInfo;
import io.k8screen.backend.data.entity.Config;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigConverter {
  Config toConfig(ConfigForm configForm);

  ConfigInfo toConfigItem(Config config);
}
