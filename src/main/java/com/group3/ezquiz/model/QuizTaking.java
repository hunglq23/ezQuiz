package com.group3.ezquiz.model;

import java.util.List;

import com.group3.ezquiz.model.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "_quiz_taking")
public class QuizTaking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Boolean isAssigned;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "learner_id")
  private User learner;

  @ManyToOne
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  @OneToMany(mappedBy = "quizTaking")
  private List<Attempt> attempts;
}
