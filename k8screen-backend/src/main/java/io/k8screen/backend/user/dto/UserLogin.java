package io.k8screen.backend.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserLogin(@NotNull @NotEmpty String username, @NotNull @NotEmpty String password) {}
