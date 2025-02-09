package io.k8screen.backend.util;

import java.util.Collection;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomUserDetails extends User {
  private final String userId;

  public CustomUserDetails(
      final @NotNull String userId,
      final @NotNull String username,
      final @NotNull String password,
      final @NotNull Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.userId = userId;
  }
}
