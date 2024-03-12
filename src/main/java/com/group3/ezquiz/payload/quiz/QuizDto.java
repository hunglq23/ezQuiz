package com.group3.ezquiz.payload.quiz;

import lombok.*;

import java.util.UUID;

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
    private String timeString;

    public String timeString(){
        return timeString.substring(0, "yyyy-MM-dd HH:mm:ss".length());
    }
}

