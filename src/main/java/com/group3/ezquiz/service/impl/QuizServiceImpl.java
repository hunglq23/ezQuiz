package com.group3.ezquiz.service.impl;

import java.time.LocalDateTime;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.payload.quiz.QuizResult;
import com.group3.ezquiz.utils.Utility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.group3.ezquiz.exception.InvalidAttemptException;
import com.group3.ezquiz.exception.InvalidQuestionException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Attempt;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.QuestionToLearner;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.payload.quiz.attempt.AttemptDto;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.service.IAttemptService;
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
  private final IUserService userService;
  private final IQuestionService questionService;
  private final IAttemptService attemptService;

  @Override
  public Quiz getDraftQuiz(HttpServletRequest request) {
    User userRequesting = userService.getUserRequesting(request);
    Quiz quiz = Quiz.builder()
        .title("")
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

  @Override
  public ResponseEntity<?> handleQuizUpdatingRequest(
      HttpServletRequest request,
      UUID id,
      QuizDetailsDto dto) {
    Quiz quiz = getQuizByRequestAndID(request, id);

    if (quiz.getQuestions().size() == 0) {
      return new ResponseEntity<>(
          MessageResponse.builder()
              .message("The number of questions must be greater than 0!")
              .timestamp(LocalDateTime.now())
              .build(),
          HttpStatus.BAD_REQUEST);
    }

    quiz.setImageUrl(dto.getImageUrl());
    quiz.setTitle(dto.getTitle());
    quiz.setIsExam(dto.getIsExam());
    quiz.setDescription(dto.getDescription());
    quiz.setIsDraft(false);

    quizRepo.save(quiz);

    return ResponseEntity.ok(
        MessageResponse.builder()
            .message("Saved successfully!")
            .timestamp(LocalDateTime.now())
            .build());
  }

  @Override
  public QuizToLearner getQuizByLearnerForTaking(HttpServletRequest request, UUID id) {
    User learner = userService.getUserRequesting(request);
    Quiz quizById = getQuizById(id);

    List<QuestionToLearner> questions = new ArrayList<>();

    Map<Long, String> answers;
    for (Question quest : quizById.getQuestions()) {
      answers = new HashMap<>();
      for (Answer answer : quest.getAnswers()) {
        answers.put(answer.getId(), answer.getText());
      }

      questions.add(
          QuestionToLearner.builder()
              .id(quest.getId())
              .text(quest.getText())
              .answers(answers)
              .numberOfCorrect(questionService
                  .getCorrectAnswerNumberInQuestion(quest.getId()))
              .build());
    }

    AttemptDto newAttempt = attemptService.getNewAttempt(learner, quizById);

    return QuizToLearner.builder()
        .id(quizById.getId())
        .title(quizById.getTitle())
        .questions(questions)
        .attempt(newAttempt)
        .build();
  }

  @Override
  public ResponseEntity<?> handleAnswersChecking(
      HttpServletRequest request,
      UUID quizId,
      Long questId,
      String questIndex,
      Map<String, String> params) {

    User learner = userService.getUserRequesting(request);
    Quiz quiz = getQuizById(quizId);

    Attempt lastAttempt = attemptService.getLastAttemptByLearnerAndQuiz(learner, quiz);
    if (lastAttempt.getEndedAt() != null || lastAttempt.getResult() != null) {
      throw new InvalidAttemptException("This attempt was finished and cannot be changed!");
    }

    Question uncheckQuestion = questionService.getByIdAndQuiz(questId, quiz);
    if (uncheckQuestion.getAnswers().size() != params.size()) {
      throw new InvalidQuestionException("Number of submited answers was wrong!");
    }
    return questionService
        .checkQuestionAnswers(lastAttempt, uncheckQuestion.getId(), params, questIndex);
  }

  private Quiz getQuizById(UUID id) {
    return quizRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found quiz by ID " + id));
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
          cellIterator.next();

          int currentAnswerIndex = 0;
          int validAnswerCount = 0;
          int correctAnswerNumber = 0;

          // Parse Answers from the subsequent cells until the end of the row
          while (cellIterator.hasNext()) {
            Cell answerCell = cellIterator.next();
            String answerText = getCellValue(answerCell).trim();
            if (answerText.isEmpty() || answerText.length() > 512) { // invalid answer text
              validQuestion = false;
              log.error("> 512 ANS");
            } else { // valid answer text
              validAnswerCount++;
            }
            currentAnswerIndex++;

            Answer answer = Answer.initFalseAnswer(question, answerText);
            if (correctAnswerIndexes.contains(currentAnswerIndex)) {
              answer.setIsCorrect(true);
              correctAnswerNumber++;
            }
            answers.add(answer);
          }

          if (validAnswerCount < 2) {
            validQuestion = false;
            log.error("At least 2 valid answer of a question!");
          }

          // For single choice questions, find the matching Answer for the correct answer
          // and mark it as correct
          if (questionType.equalsIgnoreCase("single choice")) {
            if (correctAnswerNumber != 1) {
              validQuestion = false;
              log.error("single != 1");
            }
          }

          // For multiple choice questions, parse the correct answer indices and mark the
          // corresponding Answers as correct
          else if (questionType.equalsIgnoreCase("multiple choice")) {
            if (correctAnswerNumber < 2) {
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

  @Override
  public ResponseEntity<?> handleAnswerSelectedByLearnerResp(
      HttpServletRequest request,
      UUID quizId,
      Long answerId) {

    User learner = userService.getUserRequesting(request);
    Quiz quiz = getQuizById(quizId);

    Attempt attempt = attemptService.getLastAttemptByLearnerAndQuiz(learner, quiz);
    Question question = questionService.getQuestionOfAnswerId(answerId, quizId);
    Integer totalCorrectNum = questionService.getCorrectAnswerNumberInQuestion(question.getId());
    Integer ansNumSelected = attemptService.getAnsNumSelected(attempt, answerId, question);
    if (ansNumSelected > totalCorrectNum) {
      throw new InvalidAttemptException("The selected num > total correct ans num!");
    }

    return ResponseEntity.ok(MessageResponse.builder()
        .message("Selected an answer!")
        .ansNumRemain(totalCorrectNum - ansNumSelected)
        .timestamp(LocalDateTime.now())
        .build());
  }

  @Override
  public AttemptDto handleFinishQuizAttempt(HttpServletRequest request, UUID quizId) {
    Quiz quiz = getQuizById(quizId);
    User learner = userService.getUserRequesting(request);
    Attempt attempt = attemptService.getLastAttemptByLearnerAndQuiz(learner, quiz);
    if (attempt.getEndedAt() == null && attempt.getResult() == null) {
      Attempt saved = attemptService.saveResultAndfinishAttempt(attempt);
      Integer total = saved.getTotalQuestNum();
      Integer correct = saved.getCorrectNum();
      Integer incorrect = saved.getIncorrectNum();
      Integer incomplete = total - correct - incorrect;
      if (incomplete < 0) {
        log.error("Negative value", new InvalidAttemptException("Incomplete Question Num < 0"));
      }
      return AttemptDto.builder()
          .bestResult(attemptService.getBestResult(quiz, learner))
          .totalQuestNum(total)
          .correctQuestNum(correct)
          .incorrectQuestNum(incorrect)
          .incompleteQuestNum(incomplete)
          .result(saved.getResult())
          .startedAt(saved.getStartedAt())
          .endedAt(saved.getEndedAt())
          .build();
    }
    throw new InvalidAttemptException("Invalid Attempt");
  }

  @Override
  public QuizResult getQuizResult(HttpServletRequest request, UUID quizId) {

    User learner = userService.getUserRequesting(request);
    Quiz quiz = getQuizById(quizId);
    Attempt attempt = attemptService.getLastAttemptByLearnerAndQuiz(learner, quiz);
    attempt.getResponses();
    return null;
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

  public Page<QuizDto> getQuizInLibrary(
      HttpServletRequest http,
      String sortOrder,
      Boolean isDraft,

      Pageable pageable) {
    User userRequesting = userService.getUserRequesting(http);
    Page<Quiz> quizByCreator;
    if (isDraft != null) {
      quizByCreator = quizRepo.findByCreatorAndIsDraftAndSort(userRequesting, isDraft, sortOrder, pageable);
    } else {
      quizByCreator = quizRepo.findByCreatorAndSort(userRequesting, sortOrder, pageable);
    }
    Page<QuizDto> quizDtoList = quizByCreator.map(this::mapToQuizDto);

    quizDtoList.forEach(objectDto -> objectDto.setTimeString(
        Utility.calculateTimeElapsed(
            Utility.convertStringToTimestamp(objectDto.timeString(), "yyyy-MM-dd HH:mm:ss"))));
    return quizDtoList;
  }

  private QuizDto mapToQuizDto(Quiz quiz) {
    return QuizDto.builder()
        .id(quiz.getId())
        .type("Quiz")
        .title(quiz.getTitle())
        .description(quiz.getDescription())
        .image(quiz.getImageUrl())
        .isDraft(quiz.getIsDraft())
        .itemNumber(quiz.getQuestions().size())
        .timeString(quiz.getCreatedAt().toString())
        .build();
  }

  @Override
  public void deleteQuiz(UUID id) {
    Optional<Quiz> optionalQuiz = quizRepo.findById(id);
    if (optionalQuiz.isPresent()) {
      Quiz existedQuiz = optionalQuiz.get();
      quizRepo.delete(existedQuiz);
    } else {
      throw new ResourceNotFoundException("Quiz with id " + id + "not found!");
    }
  }

  @Override
  public List<Quiz> searchQuizUUID(HttpServletRequest request, String search) {
    List<Quiz> data = quizRepo.searchQuizUUID(search);
    return data;
  }

  @Override
  public List<Quiz> getListQuizUUID(HttpServletRequest request) {
    List<Quiz> data = quizRepo.findQuizUUID();
    return data;
  }
}
