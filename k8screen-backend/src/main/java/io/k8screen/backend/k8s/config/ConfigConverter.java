package io.k8screen.backend.k8s.config;

import io.k8screen.backend.k8s.config.dto.ConfigInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigConverter {
  ConfigInfo toConfigInfo(Config config);
}
