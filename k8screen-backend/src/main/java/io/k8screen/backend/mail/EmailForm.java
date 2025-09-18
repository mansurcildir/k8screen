package io.k8screen.backend.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EmailForm(@NotNull @NotEmpty @Email String email) {}
