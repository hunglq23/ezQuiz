package com.group3.ezquiz.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {

    private String text;
    private Boolean isCorrect;

}
