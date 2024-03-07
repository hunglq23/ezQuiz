package com.group3.ezquiz.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
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
@Table(name = "_answer")
public class Answer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "question_id")
  @JsonBackReference
  private Quest question;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private Boolean isCorrect = false;

  @Override
  public String toString() {
    return "Answer [id=" + id +
        ", questionId=" + question.getId() +
        ", text=" + text +
        ", isCorrect=" + isCorrect + "]";
  }

  public Answer(String text) {
    this.text = text;
  }

}
