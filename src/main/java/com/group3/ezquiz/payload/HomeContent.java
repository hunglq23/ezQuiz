package com.group3.ezquiz.payload;

import java.util.ArrayList;
import java.util.List;

import com.group3.ezquiz.payload.quiz.QuizDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeContent {

  @Builder.Default
  private final String[] COLORS = {
      "#FFC107", "#FF5722", "#4CAF50",
      "#2196F3", "#9C27B0", "#ec3c6c",
      "#04cc84", "#6c6c6c", "#8c54c4" };
  private final List<Integer> colorIndexUsed = new ArrayList<>();

  private List<QuizDto> highlight;
  private List<QuizDto> recentQuiz;

  public List<List<QuizDto>> categories() {
    return List.of(highlight, recentQuiz);
  }

  public String getTitle(int index) {
    List<String> titles = List.of("Ice Breaker", "Recent Quizzes", "Popular Quizzes");
    return titles.get(index);
  }

  public String randomBgColor() {
    Integer randomIndex = null;
    do {
      randomIndex = (int) (Math.random() * COLORS.length);
      if (colorIndexUsed.size() == COLORS.length) {
        colorIndexUsed.clear();
      }
    } while (colorIndexUsed.contains(randomIndex));
    colorIndexUsed.add(randomIndex);
    return COLORS[randomIndex];
  }

}
