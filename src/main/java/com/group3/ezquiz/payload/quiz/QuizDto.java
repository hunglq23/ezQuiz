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
    @Builder.Default
    private String type = "Quiz";
    private UUID id;
    private String title;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isDraft;
    private Integer itemNumber;
    private Timestamp timestamp;
    private String creatorName;

    public String getTimeString() {
        return MyUtils.calculateTimeElapsed(timestamp);
    }
}
