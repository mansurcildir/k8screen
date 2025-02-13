package io.k8screen.backend.util;

import io.k8screen.backend.core.exception.ItemNotFoundException;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.repository.UserRepository;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(final @NotNull UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final @NotNull String username)
      throws UsernameNotFoundException {
    final User user =
        this.userRepository
            .findByUsername(username)
            .orElseThrow(() -> new ItemNotFoundException("User not found"));

    return new CustomUserDetails(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRoles().stream()
            .map((role) -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
            .collect(Collectors.toList()));
  }
}
