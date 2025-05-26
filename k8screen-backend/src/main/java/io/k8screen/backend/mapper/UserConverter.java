package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.data.dto.user.UserRegister;
import io.k8screen.backend.data.entity.User;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserConverter {
  User toUser(@NotNull UserRegister userRegister);

  @Mapping(
      target = "roles",
      expression = "java(user.getRoles().stream().map(role -> role.getName()).toList())")
  UserInfo toUserInfo(@NotNull User user);
}
