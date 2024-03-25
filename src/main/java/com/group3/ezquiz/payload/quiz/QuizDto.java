package com.group3.ezquiz.payload.quiz;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

import com.group3.ezquiz.utils.MyUtils;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
    private UUID id;
    private String type;
    private String title;
    private String description;
    private String image;
    private Boolean isDraft;
    private Integer itemNumber;
    private Timestamp timestamp;

    public String getTimeString() {
        return MyUtils.calculateTimeElapsed(timestamp);
    }
}
