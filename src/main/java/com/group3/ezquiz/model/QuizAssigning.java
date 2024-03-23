package com.group3.ezquiz.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_quiz_assigning")
public class QuizAssigning {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  @ManyToOne
  @JoinColumn(name = "classroom_id")
  private Classroom classroom;

  private String note;

  private Integer maxAttempt;

  private Boolean questionShuffled;

  private Boolean answerShuffled;

  private Integer durationInMins;

  private Date startDate;

  private Date dueDate;

}
