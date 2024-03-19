package com.group3.ezquiz.model;

import java.util.List;

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
  private List<Answer> answers;

  @ManyToMany(mappedBy = "questions")
  private List<Quiz> quizList;
}
