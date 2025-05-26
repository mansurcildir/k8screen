package io.k8screen.backend.data.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record GoogleToken(
    @NotNull @JsonProperty("access_token") String accessToken,
    @NotNull @JsonProperty("expires_in") String expiresIn,
    @NotNull String scope,
    @NotNull @JsonProperty("token_type") String tokenType,
    @NotNull @JsonProperty("id_token") String idToken) {}
