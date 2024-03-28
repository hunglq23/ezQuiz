package com.group3.ezquiz.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_question")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String text;

  @Column
  private Boolean isActive;

  @Column
  private Boolean isPublic;

  @ManyToOne
  @JoinColumn(name = "created_id")
  private User creator;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
  @Size(min = 2, max = 6, message = "Number of answers must be between 2 and 6")
  private List<Answer> answers;

  @ManyToMany(mappedBy = "questions")
  private List<Quiz> quizList;

  public void addAnswer(Answer answer) {
    if (answers == null) {
      answers = new ArrayList<>();
    }
    answers.add(answer);
    answer.setQuestion(this);
  }

  public Boolean isSingleChoice() {
    return type.equals("single-choice");
  }

  public Boolean isMultipleChoice() {
    return type.equals("multiple-choice");
  }

}
