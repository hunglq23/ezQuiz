package com.group3.ezquiz.payload.question.answer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnswerResult {

  private String text;
  private Boolean isCorrect;
  private Boolean isSelected;
}
