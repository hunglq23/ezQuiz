package com.group3.ezquiz.payload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
    private String code;
    private String title;
    private String description;
    private Boolean isActive;
    private Boolean isExamOnly;
}