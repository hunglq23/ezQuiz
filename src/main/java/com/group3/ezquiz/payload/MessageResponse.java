package com.group3.ezquiz.payload;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {
  private String message;
  private LocalDateTime timestamp;
  private Integer ansNumRemain;
}
