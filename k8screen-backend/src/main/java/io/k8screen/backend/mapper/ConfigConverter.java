package io.k8screen.backend.mapper;

import io.k8screen.backend.data.config.ConfigForm;
import io.k8screen.backend.data.config.ConfigItem;
import io.k8screen.backend.data.entity.Config;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigConverter {
  Config toConfig(ConfigForm configForm);

  ConfigItem toConfigItem(Config config);
}
