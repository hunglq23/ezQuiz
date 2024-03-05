package com.group3.ezquiz.service;

import java.util.Map;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;

public interface IQuestionService {

  Question createNewQuestionOfQuiz(Quiz quiz, String type, String questionText, Map<String, String> params);

}
