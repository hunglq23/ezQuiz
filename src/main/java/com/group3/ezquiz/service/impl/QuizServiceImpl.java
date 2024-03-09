package com.group3.ezquiz.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.group3.ezquiz.exception.InvalidQuestionException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.service.IQuestionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {

  private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

  private final QuizRepo quizRepo;
  private final IQuestionService questionService;
  private final IUserService userService;

  @Override
  public Quiz getDraftQuiz(HttpServletRequest request) {
    User userRequesting = userService.getUserRequesting(request);
    Quiz quiz = Quiz.builder()
        .isDraft(true)
        .isEnable(true)
        .isExam(false)
        .creator(userRequesting)
        .build();
    Quiz saved = quizRepo.save(quiz);
    return saved;
  }

  @Override
  public Quiz getQuizByRequestAndID(HttpServletRequest request, UUID id) {
    User userRequesting = userService.getUserRequesting(request);
    return quizRepo
        .findByIdAndCreator(id, userRequesting)
        .orElseThrow(
            () -> new ResourceNotFoundException("Not found your quiz with ID " + id));
  }

  @Override
  public Quiz handleQuestionCreatingInQuiz(
      Quiz quiz,
      String type,
      String questionText,
      Map<String, String> params) {
    Question newQuestion = questionService
        .createNewQuestionOfQuiz(quiz, type, questionText, params);
    quiz.getQuestions().add(newQuestion);
    quizRepo.save(quiz);
    return quizRepo.save(quiz);
  }

  @Transactional
  @Override
  public List<Question> importQuizDataFromExcel(HttpServletRequest request, MultipartFile file, UUID id) {
    Quiz quiz = getQuizByRequestAndID(request, id);

    List<Question> errorQuestions = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream()) {
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.getSheetAt(0); // Assuming the quiz data is in the first sheet
      Iterator<Row> rowIterator = sheet.iterator();

      // Check if there is at least one row
      if (rowIterator.hasNext()) {
        rowIterator.next(); // Skip header row
        rowIterator.next();

        List<Question> questions = quiz.getQuestions();

        // Iterate over each row in the sheet starting from the third row
        while (rowIterator.hasNext()) {
          boolean validQuestion = true;
          Row row = rowIterator.next();
          Iterator<Cell> cellIterator = row.cellIterator();

          // Parse question text from the first cell of the row
          Cell questionCell = cellIterator.next();
          String questionText = getCellValue(questionCell).trim();
          if (questionText.length() > 512) { // validtion question text max length
            log.error("> 512");
            validQuestion = false;
          }
          Question question = Question.builder()
              .text(questionText)
              .build();

          // Parse question type from the second cell of the row
          Cell typeCell = cellIterator.next();
          String questionType = getCellValue(typeCell).trim();
          if (!List.of("Single Choice", "Multiple Choice").contains(questionType)) {
            validQuestion = false;
            log.error("type " + questionType);

          }
          question.setType(questionType);

          List<Answer> answers = new ArrayList<>();

          String correctAnswerText = getCellValue(row.getCell(2)).trim();
          Set<Integer> correctAnswerIndexes = parseCorrectAnswers(correctAnswerText);
          if (correctAnswerIndexes.contains(-1)) {
            validQuestion = false;
          }

          int currentAnswerIndex = 0;

          cellIterator.next();

          // Parse Answers from the subsequent cells until the end of the row
          boolean hasAnswer = false; // Đánh dấu khi có ít nhất một câu trả lời không rỗng
          while (cellIterator.hasNext()) {
            Cell answerCell = cellIterator.next();
            String answerText = getCellValue(answerCell).trim();
            if (answerText.isEmpty() || answerText.length() > 512) { // invalid answer text
              validQuestion = false;
              log.error("> 512 ANS");
            } else { // valid answer text
              currentAnswerIndex++;
              hasAnswer = true;
            }

            Answer answer = Answer.initFalseAnswer(question, answerText);
            if (correctAnswerIndexes.contains(currentAnswerIndex)) {
              answer.setIsCorrect(true);
            }
            answers.add(answer);
          }

          if (!hasAnswer) {
            validQuestion = false;
            log.error("Question has no answers");
          }

          int countCorrectAnswer = correctAnswerIndexes.size();

          // For single choice questions, find the matching Answer for the correct answer
          // and mark it as correct
          if (questionType.equalsIgnoreCase("single choice")) {
            if (countCorrectAnswer != 1) {
              validQuestion = false;
              log.error("single != 1");

            }
          }

          // For multiple choice questions, parse the correct answer indices and mark the
          // corresponding Answers as correct
          else if (questionType.equalsIgnoreCase("multiple choice")) {
            if (countCorrectAnswer < 2) {
              validQuestion = false;
              log.error(" MTP < 2");
            }
          }

          question.setAnswers(answers);
          if (!validQuestion) {
            errorQuestions.add(question);
          } else {
            questions.add(question);
          }

        }

        quiz.setQuestions(questions);
        quizRepo.save(quiz);

      }
    } catch (IOException | EncryptedDocumentException e) {
      e.printStackTrace();
    }

    return errorQuestions;
  }

  private String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        return String.valueOf(cell.getNumericCellValue());
      default:
        return ""; // Return empty string for other cell types
    }
  }

  private Set<Integer> parseCorrectAnswers(@NotNull @NotBlank String correctAnswerText) throws NumberFormatException {
    log.info("cat " + correctAnswerText);
    Set<Integer> correctAnswers = new HashSet<>();
    String[] indexes = correctAnswerText.split(",");
    log.info("list " + indexes.toString());
    for (String index : indexes) {
      log.info("Parsing " + index);
      int answerIndex = (int) Float.parseFloat(index);
      if (answerIndex != (float) Float.parseFloat(index) || answerIndex < 1 || answerIndex > 6) {
        answerIndex = -1;
      }
      correctAnswers.add(answerIndex);

    }

    return correctAnswers;
  }

  @Override
  public ByteArrayInputStream getDataDownloaded(Quiz quiz) throws IOException {
    String[] columns = { "Question", "Type", "Correct Answer", "Choice 1", "Choice 2", "Choice 3", "Choice 4",
        "Choice 5", "Choice 6" };

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Quiz Data");
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);

      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setFont(headerFont);

      // Create the header row
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < columns.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columns[i]);
        cell.setCellStyle(headerCellStyle);
      }

      // Fill data rows
      int rowNum = 1;
      for (Question question : quiz.getQuestions()) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(question.getText());
        row.createCell(1).setCellValue(question.getType());

        List<Answer> answers = question.getAnswers();
        int answerNum = 2;
        for (Answer answer : answers) {
          Cell answerCell = row.createCell(answerNum++);
          answerCell.setCellValue(answer.getText());
          if (answer.getIsCorrect()) {
            CellStyle correctCellStyle = workbook.createCellStyle();
            answerCell.setCellStyle(correctCellStyle);
          }
        }

        // Fill remaining cells with empty strings
        for (int i = answerNum; i < columns.length; i++) {
          row.createCell(i).setCellValue("");
        }
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    }
  }

}
