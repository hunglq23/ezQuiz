package com.group3.ezquiz.payload;

import java.sql.Date;

import lombok.*;

@Setter
@Getter
@Builder
@Data
public class AssignedQuizDto {

    private Date startDate;
    private Date dueDate;
    private String note;
    private Integer maxAttempt;
    private Integer durationInMins;
    private boolean shuffleQuestions;
    private boolean shuffleAnswers;
    private Long selectedClassroom;
}
