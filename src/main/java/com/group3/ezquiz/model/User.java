package com.group3.ezquiz.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

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
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean isEnable;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean isVerified;

  @Column(length = 15)
  private String phone;

  @Column(length = 500)
  private String avatarUrl;

  private String note;

  private String token;

  private Long createdId;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private Timestamp updatedAt;

  @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
  private List<Quiz> quizCreated;

}
