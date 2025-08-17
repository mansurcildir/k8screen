package io.k8screen.backend.account;

import io.k8screen.backend.account.dto.AccountForm;
import io.k8screen.backend.account.dto.AccountItem;
import io.k8screen.backend.auth.dto.AccountType;
import io.k8screen.backend.exception.ForbiddenException;
import io.k8screen.backend.user.User;
import io.k8screen.backend.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
  private final @NotNull AccountRepository accountRepository;
  private final @NotNull UserRepository userRepository;

  public @NotNull List<AccountItem> getGoogleAccounts(final @NotNull UUID userUuid) {
    return this.accountRepository.findAllByUserUuidAndAccountType(
        userUuid, AccountType.GOOGLE.toString());
  }

  public @NotNull List<AccountItem> getGithubAccounts(final @NotNull UUID userUuid) {
    return this.accountRepository.findAllByUserUuidAndAccountType(
        userUuid, AccountType.GITHUB.toString());
  }

  public void connectAccount(
      final @NotNull UUID userUuid,
      final @NotNull AccountForm accountForm,
      final @NotNull AccountType type) {

    final String subjectId = accountForm.getSubjectId();
    final String username = accountForm.getUsername();
    final String email = accountForm.getEmail();
    final String avatarUrl = accountForm.getAvatarUrl();
    final String accountType = type.toString();

    final boolean accountExists =
        this.accountRepository.existsBySubjectIdAndAccountType(subjectId, accountType);

    if (accountExists) {
      throw new ForbiddenException("accountConnected");
    }

    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ForbiddenException("accessDenied"));

    final Account account = new Account();
    account.setUuid(UUID.randomUUID());
    account.setAccountType(accountType);
    account.setSubjectId(subjectId);
    account.setUsername(username);
    account.setAvatarUrl(avatarUrl);
    account.setEmail(email);
    account.setUser(user);
    account.setCreatedAt(Instant.now());

    this.accountRepository.save(account);
  }

  public void deleteAccount(final @NotNull UUID userUuid, final @NotNull UUID accountUuid) {

    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ForbiddenException("accessDenied"));

    final long accountsCount = this.accountRepository.countByUserUuid(userUuid);

    if (user.getPassword() == null && accountsCount < 2) {
      throw new ForbiddenException("setPassword");
    }

    this.accountRepository.deleteByUserUuidAndUuid(userUuid, accountUuid, Instant.now());
  }
}
