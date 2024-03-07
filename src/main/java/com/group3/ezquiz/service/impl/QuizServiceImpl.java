package com.group3.ezquiz.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
          }
          question.setType(questionType);

          List<Answer> answers = new ArrayList<>();

          // Skip the cell containing the correct answer(s)
          cellIterator.next();

          // Parse Answers from the subsequent cells until the end of the row
          while (cellIterator.hasNext()) {
            Cell answerCell = cellIterator.next();
            String answerText = getCellValue(answerCell).trim();
            if (answerText.length() > 512) {
              validQuestion = false;
            }

            Answer answer = Answer.initFalseAnswer(question, answerText);
            answers.add(answer);
          }

          String correctAnswerText = getCellValue(row.getCell(2)).trim();
          List<Integer> correctAnswerIndexes = parseCorrectAnswers(correctAnswerText);
          int countCorrectAnswer = correctAnswerIndexes.size();

          // For single choice questions, find the matching Answer for the correct answer
          // and mark it as correct
          if (questionType.equalsIgnoreCase("single choice")) {
            if (countCorrectAnswer < 1 || countCorrectAnswer >= 2) {
              // throw new InvalidQuestionException("Only 1 answer selected choice!");
              validQuestion = false;
            }
            for (Answer answer : answers) {
              if (answer.getText().equalsIgnoreCase(correctAnswerText)) {
                answer.setIsCorrect(true);
              }
            }

          }

          // For multiple choice questions, parse the correct answer indices and mark the
          // corresponding Answers as correct
          else if (questionType.equalsIgnoreCase("multiple choice")) {
            if (countCorrectAnswer < 2) {
              // throw new InvalidQuestionException("At least 2 answer selected!");
              validQuestion = false;
            }
            for (int index : correctAnswerIndexes) {
              if (index >= 0 && index < answers.size()) {
                answers.get(index).setIsCorrect(true);
              }
            }

          }

          question.setAnswers(answers);
          questions.add(question);
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

  private List<Integer> parseCorrectAnswers(String correctAnswerText) {
    List<Integer> correctAnswers = new ArrayList<>();
    if (correctAnswerText != null && !correctAnswerText.isEmpty()) {
      String[] indexes = correctAnswerText.split(",");
      for (String index : indexes) {
        int answerIndex = -69;
        try {
          answerIndex = Integer.parseInt(index.trim()) - 1;
          if (answerIndex > 0 && answerIndex < 7) {
            correctAnswers.add(answerIndex);
          }
          throw new NumberFormatException();

        } catch (NumberFormatException e) {
          log.error("Invalid correct ans column: " + answerIndex);
        }
      }
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
