package com.group3.ezquiz.payload.quiz;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
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

