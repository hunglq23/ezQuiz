package com.group3.ezquiz.payload.question;

import java.util.List;

import com.group3.ezquiz.payload.question.answer.AnswerResult;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionResult {

  private String text;
  private Boolean isCorrect;
  private List<AnswerResult> answers;
}
