package io.k8screen.backend.data.dto.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserConfig {
  @NotNull @NotEmpty private String config;
}
