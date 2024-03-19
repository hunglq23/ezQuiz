package com.group3.ezquiz.model;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "_attempt")
public class Attempt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "taking_id")
  private QuizTaking quizTaking;

  @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
  private List<UserResponse> userResponse;

  private String result;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp started_at;

  private Timestamp ended_at;

}
