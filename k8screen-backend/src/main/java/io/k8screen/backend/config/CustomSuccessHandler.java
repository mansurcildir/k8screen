package io.k8screen.backend.config;

import io.k8screen.backend.data.user.UserForm;
import io.k8screen.backend.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {
  @Value("${k8screen-frontend.success-url}")
  private String apiSuccessURL;

  private final UserService userService;

  public CustomSuccessHandler(final @NotNull UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(
      final @NotNull HttpServletRequest request,
      final @NotNull HttpServletResponse response,
      final @NotNull Authentication authentication)
      throws IOException, ServletException {
    if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
      final DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
      final String username = userDetails.getAttribute("email");
      System.out.println(username);

      try {
        this.userService.findByUsername(username);
      } catch (NoSuchElementException e) {
        final UserForm userForm =
            UserForm.builder().username(username).email(username).password("dummy").build();
        this.userService.create(userForm);
      }
      response.setStatus(HttpServletResponse.SC_OK);
      response.sendRedirect(this.apiSuccessURL);
    }
  }
}
