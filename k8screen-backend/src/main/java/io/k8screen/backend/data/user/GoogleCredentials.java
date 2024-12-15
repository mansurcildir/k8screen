package io.k8screen.backend.data.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GoogleCredentials {
  private @NotNull String code;
  private @NotNull @JsonProperty("client_id") String clientId;
  private @NotNull @JsonProperty("client_secret") String clientSecret;
  private @NotNull @JsonProperty("redirect_uri") String redirectUri;
  private @NotNull @JsonProperty("grant_type") String grantType;
}
