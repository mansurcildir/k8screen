package io.k8screen.backend.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Column(name = "max_config_count", nullable = false)
  private long maxConfigCount;
}
