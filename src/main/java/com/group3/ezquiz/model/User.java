package com.group3.ezquiz.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "full_name", nullable = false)
  private String fullName;
  @Column(name = "is_enable", nullable = false, columnDefinition = "boolean default false")
  private Boolean isEnable;
  @Column(name = "is_verified", nullable = false, columnDefinition = "boolean default false")
  private Boolean isVerified;
  @Column(name = "phone", length = 10)
  private String phone;
  @Column(name = "avatar", length = 500)
  private String avatar;
  @Column(name = "note")
  private String note;
  @Column(name = "token")
  private String token;

  @Column(name = "created_by")
  private Integer createdBy;
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp createdAt;

  @Column(name = "updated_by")
  private Integer updatedBy;
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Timestamp updateAt;
}
