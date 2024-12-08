package io.k8screen.backend.mapper;

import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.data.user.UserItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {
  User toUser(UserForm userForm);

  UserItem toUserItem(User user);
}
