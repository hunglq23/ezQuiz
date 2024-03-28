package com.group3.ezquiz.payload.quiz;

import java.sql.Date;
import java.sql.Timestamp;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.utils.MyUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAssignedDto {

  private String id;
  private Date startDate;
  private Date dueDate;
  private String note;
  private Integer maxAttempt;
  private Integer durationInMins;
  private boolean shuffleQuestions;
  private boolean shuffleAnswers;
  private Long selectedClassroom;
  private Quiz quiz;
  private String teacherName;
  private String type;
  private Timestamp createdAt;

  public String getTimeString() {
    return MyUtils.calculateTimeElapsed(createdAt);
  }

}
