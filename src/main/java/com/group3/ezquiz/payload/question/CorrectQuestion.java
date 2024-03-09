package com.group3.ezquiz.payload.question;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CorrectQuestion {
  private String message;
  private String questIndex;
  private LocalDateTime timestamp;
}
