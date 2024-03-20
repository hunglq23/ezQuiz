package com.group3.ezquiz.payload.quiz;

import java.util.List;

import com.group3.ezquiz.payload.question.QuestionResult;
import com.group3.ezquiz.payload.quiz.attempt.AttemptDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizResult {
  private AttemptDto attempt;
  private String title;
  private String description;
  private List<QuestionResult> questions;
}
