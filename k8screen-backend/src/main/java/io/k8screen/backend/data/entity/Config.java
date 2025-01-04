package io.k8screen.backend.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "configs",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Config {
  @Id
  @Column(unique = true, length = 26, nullable = false, updatable = false)
  private String id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
