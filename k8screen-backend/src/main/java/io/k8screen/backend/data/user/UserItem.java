package io.k8screen.backend.data.user;

import io.k8screen.backend.data.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserItem(
    @NotNull @NotEmpty String id,
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String password,
    @NotNull @NotEmpty String email,
    @NotNull @NotEmpty Set<Role> roles) {}
