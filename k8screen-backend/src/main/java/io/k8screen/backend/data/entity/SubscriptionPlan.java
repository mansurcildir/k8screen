package io.k8screen.backend.data.entity;

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
@Table(name = "SUBSCRIPTION_PLAN")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "NAME", unique = true, nullable = false)
  private String name;

  @Column(name = "MAX_CONFIG_COUNT", nullable = false)
  private long maxConfigCount;
}
