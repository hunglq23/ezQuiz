package com.group3.ezquiz.payload.question;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IncorrectQuestion {
  private String questIndex;
  private Map<String, Boolean> answers;
}
