package io.k8screen.backend.data.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserGoogleLogin(@NotNull @NotEmpty String code) {}
