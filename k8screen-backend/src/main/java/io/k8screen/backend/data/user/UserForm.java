package io.k8screen.backend.data.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class UserForm {
  private @NotNull @NotEmpty String username;
  private @NotNull @NotEmpty String password;
  private @NotNull @NotEmpty @Email String email;
  private String picture;
}
