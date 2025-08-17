package io.k8screen.backend.auth;

import io.k8screen.backend.auth.dto.OAuthUserInfo;
import io.k8screen.backend.user.dto.AuthResponse;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OAuthService {
  @NotNull
  AuthResponse login(@NotNull OAuthUserInfo userInfo);

  void connect(@NotNull UUID userUuid, @NotNull OAuthUserInfo userInfo);

  @Nullable
  String getPrimaryEmail(@NotNull String token);
}
