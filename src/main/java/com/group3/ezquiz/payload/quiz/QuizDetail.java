package com.group3.ezquiz.payload.quiz;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.group3.ezquiz.payload.QuestionDto;
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
public class QuizDetail {

    private UUID id;
    private String title;
    private Boolean isDraft;
    private Boolean isEnable;
    private Boolean isExam;
    private String imageUrl;
    private String description;
    private String creatorName;
    private List<QuestionDto> questions;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public String getTitle() {
        return this.title == null ? "" : this.title;
    }
}
