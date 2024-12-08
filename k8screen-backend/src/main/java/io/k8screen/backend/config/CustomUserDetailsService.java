package io.k8screen.backend.config;

import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {
  private final UserService userService;

  public CustomUserDetailsService(final @NotNull UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserItem userItem = this.userService.findByUsername(username);
    if(userItem != null)
    {
      return new User(
        userItem.username(),
        userItem.password(),
        userItem.roles().stream().map((role) -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
          .collect(Collectors.toList()));
    }
    else {
      throw new UsernameNotFoundException("Invalid username or password!");
    }
  }
}
