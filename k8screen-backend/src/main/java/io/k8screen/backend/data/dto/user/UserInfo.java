package io.k8screen.backend.data.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserInfo(
    @NotNull @NotEmpty UUID uuid,
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String email,
    String picture,
    @JsonProperty("active_config") String activeConfig,
    @NotNull @NotEmpty List<String> roles) {}
