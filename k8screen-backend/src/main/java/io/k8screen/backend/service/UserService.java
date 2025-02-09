package io.k8screen.backend.service;

import com.github.f4b6a3.ulid.UlidCreator;
import io.k8screen.backend.core.exception.ItemNotFoundException;
import io.k8screen.backend.data.dto.user.UserConfig;
import io.k8screen.backend.data.dto.user.UserForm;
import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.data.entity.Role;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.mapper.UserConverter;
import io.k8screen.backend.repository.RoleRepository;
import io.k8screen.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull UserConverter userConverter;

  public void create(final @NotNull UserForm userForm) {
    final User user = this.userConverter.toUser(userForm);
    user.setId(UlidCreator.getUlid().toString());

    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("Role not found"));
    user.setRoles(Set.of(role));

    final User createdUser = this.userRepository.save(user);
    this.userConverter.toUserItem(createdUser);
  }

  public void updateConfig(final @NotNull UserConfig userConfig, final @NotNull String userId) {
    final User user =
        this.userRepository
            .findById(userId)
            .orElseThrow(() -> new ItemNotFoundException("User not found"));
    user.setConfig(userConfig.getConfig());
    this.userConverter.toUserItem(this.userRepository.save(user));
  }

  public UserInfo findByUsername(final @NotNull String username) {
    final User user =
        this.userRepository
            .findByUsername(username)
            .orElseThrow(() -> new ItemNotFoundException("User not found"));
    return this.userConverter.toUserItem(user);
  }

  public UserInfo findById(final @NotNull String userId) {
    final User user =
        this.userRepository
            .findById(userId)
            .orElseThrow(() -> new ItemNotFoundException("User not found"));
    return this.userConverter.toUserItem(user);
  }
}
