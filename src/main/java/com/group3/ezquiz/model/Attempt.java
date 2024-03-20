package com.group3.ezquiz.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_attempt")
public class Attempt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "taking_id")
  private QuizTaking quizTaking;

  @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
  private List<UserResponse> responses;

  @Column(precision = 5, scale = 2)
  private BigDecimal result;

  private Integer totalQuestNum;

  private Integer correctNum;

  private Integer incorrectNum;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp startedAt;

  private Timestamp endedAt;

}
