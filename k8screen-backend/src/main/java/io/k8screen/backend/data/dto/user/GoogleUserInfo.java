package io.k8screen.backend.data.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record GoogleUserInfo(
    @NotNull String sub,
    @NotNull String name,
    @NotNull @JsonProperty("given_name") String givenName,
    @NotNull @JsonProperty("family_name") String familyName,
    @NotNull String picture,
    @NotNull String email,
    @NotNull @JsonProperty("email_verified") String emailVerified) {}
