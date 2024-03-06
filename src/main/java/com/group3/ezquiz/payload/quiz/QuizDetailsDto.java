package com.group3.ezquiz.payload.quiz;

import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.Quiz;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDetailsDto {

    @With
    @Size(min = Quiz.MIN_TITLE_LENGTH, message = "At least " +
            Quiz.MIN_TITLE_LENGTH + " letters of Title!")
    @Size(max = Quiz.MAX_TITLE_LENGTH, message = Quiz.MAX_TITLE_LENGTH +
            " characters are maximum!")
    private String title;

    @With
    @Size(max = Quiz.MAX_IMAGE_URL_LENGTH, message = "URL too long! " +
            Quiz.MAX_IMAGE_URL_LENGTH + " characters are maximum.")
    private String imageUrl;

    @With
    @Size(max = Quiz.MAX_DESCRIPTION_LENGTH, message = Quiz.MAX_DESCRIPTION_LENGTH +
            " characters are maximum!")
    private String description;

    private Boolean isExam;
    private MultipartFile imageFile;

    public void setTitle(final String title) {
        this.title = title.trim().replaceAll("\\s+", " ");
    }

    public void setDescription(final String description) {
        this.description = description.trim().isEmpty() ? null : description;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl.isBlank() ? null : imageUrl;
    }

}
