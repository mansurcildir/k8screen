package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.user.UserForm;
import io.k8screen.backend.data.dto.user.UserInfo;
import io.k8screen.backend.data.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {
  User toUser(UserForm userForm);

  UserInfo toUserItem(User user);
}
