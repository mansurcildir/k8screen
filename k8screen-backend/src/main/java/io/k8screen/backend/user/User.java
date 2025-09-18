package io.k8screen.backend.user;

import io.k8screen.backend.k8s.config.Config;
import io.k8screen.backend.refresh_token.RefreshToken;
import io.k8screen.backend.subscription.SubscriptionPlan;
import io.k8screen.backend.user.role.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "\"user\"")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private UUID uuid;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "active_config")
  private String activeConfig;

  @Column(name = "stripe_customer_id")
  private UUID stripeCustomerId;

  @ManyToOne
  @JoinColumn(name = "subscription_plan_id", nullable = false)
  private SubscriptionPlan subscriptionPlan;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Config> configs;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<RefreshToken> refreshTokens;

  @Builder.Default
  @ManyToMany
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
