package com.group3.ezquiz.payload;

import org.springframework.web.multipart.MultipartFile;

import com.group3.ezquiz.model.QuizUUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDetailsDto {

  @Size(min = QuizUUID.MIN_TITLE_LENGTH, message = "Title must be at least " +
      QuizUUID.MIN_TITLE_LENGTH + " characters!")
  @Size(max = QuizUUID.MAX_TITLE_LENGTH, message = QuizUUID.MAX_TITLE_LENGTH +
      " characters are maximum!")
  @NotBlank(message = "Title cannot be blank!")
  private String title;

  @Size(max = QuizUUID.MAX_IMAGE_URL_LENGTH, message = "URL too long! " +
      QuizUUID.MAX_IMAGE_URL_LENGTH + " characters are maximum.")
  private String imageUrl;

  @Size(max = QuizUUID.MAX_DESCRIPTION_LENGTH, message = QuizUUID.MAX_DESCRIPTION_LENGTH +
      " characters are maximum!")
  private String description;

  private MultipartFile imageFile;
  private Boolean isExam;
}
