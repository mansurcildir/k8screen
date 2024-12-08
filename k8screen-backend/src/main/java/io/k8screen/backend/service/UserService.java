package io.k8screen.backend.service;

import com.github.f4b6a3.ulid.UlidCreator;
import io.k8screen.backend.data.entity.Role;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserItem;
import io.k8screen.backend.mapper.UserConverter;
import io.k8screen.backend.repository.RoleRepository;
import io.k8screen.backend.repository.UserRepository;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull UserConverter userConverter;

  public UserService(
      final @NotNull UserRepository userRepository,
      final @NotNull UserConverter userConverter,
      final @NotNull RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
    this.roleRepository = roleRepository;
  }

  public UserItem create(final @NotNull UserForm userForm) {
    final User user = this.userConverter.toUser(userForm);
    user.setId(UlidCreator.getUlid().toString());

    final Role role = this.roleRepository.findByName("USER").orElseThrow();
    user.setRoles(Set.of(role));

    final User createdUser = this.userRepository.save(user);
    return this.userConverter.toUserItem(createdUser);
  }

  public UserItem findByUsername(final @NotNull String username) {
    final User user =
        this.userRepository.findByUsername(username).orElseThrow();
    return this.userConverter.toUserItem(user);
  }
}
