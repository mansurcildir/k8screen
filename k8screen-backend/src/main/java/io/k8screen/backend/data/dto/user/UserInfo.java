package io.k8screen.backend.data.dto.user;

import io.k8screen.backend.data.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserInfo(
    @NotNull @NotEmpty String id,
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String email,
    String picture,
    String config,
    @NotNull @NotEmpty Set<Role> roles) {}
