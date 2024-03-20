package com.group3.ezquiz.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user_response")
public class UserResponse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "attemp_id")
  private Attempt attempt;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "question_id")
  private Question question;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "answer_id")
  private Answer answer;

  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Timestamp timestamp;

}
