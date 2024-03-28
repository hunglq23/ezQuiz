package com.group3.ezquiz.payload;

import java.util.List;

import com.group3.ezquiz.payload.quiz.QuizAssignedDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LibraryResponse {
    private Integer totalPages;
    private List<ObjectDto> objectDtoList;
    private List<QuizAssignedDto> content;
    private Boolean exceedMaxPage;
    private Long totalElements;
}
