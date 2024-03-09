package com.group3.ezquiz.payload.quiz;

import java.util.List;
import java.util.UUID;

import com.group3.ezquiz.payload.QuestionToLearner;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizToLearner {
  private UUID id;
  private String title;
  private List<QuestionToLearner> questions;
}