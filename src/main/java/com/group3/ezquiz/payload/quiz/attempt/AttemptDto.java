package com.group3.ezquiz.payload.quiz.attempt;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttemptDto {
  private Long id;
  private BigDecimal bestResult;
  private Timestamp lastTaking;
  private Integer currentAttempt;

  private BigDecimal result;
  private Integer correctQuestNum;
  private Integer incorrectQuestNum;
  private Integer incompleteQuestNum;
  private Integer totalQuestNum;
  private Timestamp startedAt;
  private Timestamp endedAt;

  public String bestResult() {
    return bestResult + " / 100";
  }

  public String result() {
    return result + " / 100";
  }
}
