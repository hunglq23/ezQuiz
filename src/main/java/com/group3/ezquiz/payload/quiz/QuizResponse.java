package com.group3.ezquiz.payload.quiz;

import com.group3.ezquiz.payload.ObjectDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuizResponse {
    private Integer maxPage;
    private List<QuizDto> quizDtoList;
    private Boolean exceedMaxPage;
    private Long totalItemNumber;
}
