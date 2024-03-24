package com.group3.ezquiz.payload.quiz;

import java.util.List;

import com.group3.ezquiz.model.Question;
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
  private String image;
  private List<Question> questions;
}
