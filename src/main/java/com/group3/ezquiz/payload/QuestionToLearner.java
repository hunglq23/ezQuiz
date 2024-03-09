package com.group3.ezquiz.payload;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionToLearner {
  private Long id;
  private String text;
  private Map<Long, String> answers;
  private Integer numberOfCorrect;
}