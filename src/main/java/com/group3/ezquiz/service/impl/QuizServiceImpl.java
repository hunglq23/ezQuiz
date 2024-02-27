package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Quest;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.QuizDetailsDto;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.repository.QuizUUIDRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IQuestService;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.output.ByteArrayOutputStream;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {

    private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final QuizUUIDRepo quizUUIDRepo;
    private final IQuestService questionService;

    private final UserRepo userRepo;

    private final QuizRepo qRepo;

    @Override
    public QuizUUID saveAndGetDraftQuiz(HttpServletRequest http) {
        User userRequesting = getUserRequesting(http);
        QuizUUID quizUUID = QuizUUID.builder()
                .isDraft(true)
                .isEnable(true)
                .isExam(false)
                .creator(userRequesting)
                .build();
        QuizUUID saved = quizUUIDRepo.save(quizUUID);
        return saved;

    }

    @Override
    public QuizUUID getQuizByRequestAndUUID(HttpServletRequest request, UUID id) {
        User userRequesting = getUserRequesting(request);
        QuizUUID byIdAndCreator = quizUUIDRepo.findByIdAndCreator(id, userRequesting);

        if (byIdAndCreator != null) {
            return byIdAndCreator;
        }
        throw new ResourceNotFoundException("Not found your quiz with ID " + id);
    }

    @Override
    public ResponseEntity<?> handleQuizUpdatingRequest(
            HttpServletRequest request,
            UUID id,
            QuizDetailsDto dto) {
        User userRequesting = getUserRequesting(request);
        QuizUUID quiz = quizUUIDRepo.findByIdAndCreator(id, userRequesting);

        if (quiz.getQuestions().size() == 0) {
            return new ResponseEntity<>(
                    new MessageResponse("The number of questions must be greater than 0!"),
                    HttpStatus.BAD_REQUEST);
        }

        quiz.setImageUrl(dto.getImageUrl());
        quiz.setTitle(dto.getTitle());
        quiz.setIsExam(dto.getIsExam());
        quiz.setDescription(dto.getDescription());
        quiz.setIsDraft(false);

        quizUUIDRepo.save(quiz);

        return ResponseEntity.ok(new MessageResponse("Successfull!!!"));
    }

    @Override
    public String handleQuestionCreatingInQuiz(
            HttpServletRequest request,
            QuizUUID quiz,
            String type,
            String questionText,
            Map<String, String> params) {
        Quest newQuestion = questionService.createNewQuestion(request, type, questionText, params);
        quiz.getQuestions().add(newQuestion);
        quizUUIDRepo.save(quiz);
        return "redirect:/quiz/" + quiz.getId() + "/edit";
    }

    @Override
    public Page<Quiz> listAll(HttpServletRequest http, String searchTerm, Pageable pageable) {
        return qRepo.getAllQuiz(searchTerm, searchTerm, pageable);
    }

    @Override
    public void toggleQuizStatus(Integer id) {
        Quiz existedQuiz = qRepo.findQuizById(id);

        if (existedQuiz != null) {
            existedQuiz.setIsActive(!existedQuiz.getIsActive());
            qRepo.save(existedQuiz);
        }
    }

    @Override
    public boolean existedQuizByCode(String code) {
        return qRepo.existsQuizByCode(code);
    }

    @Override
    public void createQuiz(HttpServletRequest request, QuizDto quizDto) {
        if (existedQuizByCode(quizDto.getCode())) {
            throw new IllegalArgumentException("A quiz with the same code already existed");
        }
        qRepo.save(
                Quiz.builder()
                        .code(quizDto.getCode())
                        .title(quizDto.getTitle())
                        .description(quizDto.getDescription())
                        .isExamOnly(quizDto.getIsExamOnly())
                        .isActive(quizDto.getIsActive())
                        .createdBy(getUserRequesting(request))
                        .build());
    }

    @Override
    public Quiz getQuizById(String stringId) {
        Integer id = getIntegerId(stringId);
        if (id != null) {
            Optional<Quiz> byId = qRepo.findById(id);
            if (byId.isPresent())
                return byId.get();
        }
        throw new ResourceNotFoundException("Not found quiz with ID: " + stringId);
    }

    @Override
    public Quiz findQuizById(Integer id) {
        Quiz quizById = qRepo.findQuizById(id);
        if (quizById != null) {
            return quizById;
        }
        throw new ResourceNotFoundException("Cannot find quiz with" + id);
    }

    @Override
    public void deleteQuiz(Integer id) {
        Optional<Quiz> optionalQuiz = qRepo.findById(id);
        if (optionalQuiz.isPresent()) {
            Quiz existedQuiz = optionalQuiz.get();
            qRepo.delete(existedQuiz);
        } else {
            throw new ResourceNotFoundException("Quiz with id " + id + "not found!");
        }
    }

    @Override
    public void updateQuiz(HttpServletRequest request, Integer id, QuizDto updateQuiz) {

        Quiz existedQuiz = findQuizById(id);
        // if(existedQuizByCode(updateQuiz.getCode())){
        // throw new IllegalArgumentException("A quiz with the same code already
        // existed");
        // }
        Quiz saveQuiz = Quiz.builder()
                // unchangeable
                .id(existedQuiz.getId())
                .createdAt(existedQuiz.getCreatedAt())
                .createdBy(existedQuiz.getCreatedBy())
                // to update
                .code(updateQuiz.getCode())
                .title(updateQuiz.getTitle())
                .description(updateQuiz.getDescription())
                .isActive(updateQuiz.getIsActive())
                .isExamOnly(updateQuiz.getIsExamOnly())
                .build();
        qRepo.save(saveQuiz);
    }

    private User getUserRequesting(HttpServletRequest http) {
        Principal userPrincipal = http.getUserPrincipal();
        String requestingUserEmail = userPrincipal.getName();
        User requestingUser = userRepo.findByEmail(requestingUserEmail);
        return requestingUser;
    }

    private Integer getIntegerId(String stringId) {

        try {
            return Integer.parseInt(stringId);

        } catch (NumberFormatException e) {
            log.error("Cannot parse ID " + stringId + " to Integer");
        }
        return null;
    }

    @Transactional
    @Override
    public void importQuizDataFromExcel(HttpServletRequest request, MultipartFile file, UUID id) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the quiz data is in the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Check if there is at least one row
            if (rowIterator.hasNext()) {
                Row firstRow = rowIterator.next();
                // String quizTitle = getCellValue(firstRow.getCell(0)).trim();

                QuizUUID quiz = getQuizByRequestAndUUID(request, id);
                // quiz.setTitle(quizTitle);

                List<Quest> questions = quiz.getQuestions();

                // Iterate over each row in the sheet starting from the second row
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    Quest question = new Quest();
                    question.setQuiz(quiz);

                    // Parse question text from the first cell of the row
                    Cell questionCell = cellIterator.next();
                    String questionText = getCellValue(questionCell).trim();
                    question.setText(questionText);

                    // Parse question type from the second cell of the row
                    Cell typeCell = cellIterator.next();
                    String questionType = getCellValue(typeCell).trim();
                    question.setType(questionType);

                    List<Answer> Answers = new ArrayList<>();

                    cellIterator.next(); // Skipping the answer cell

                    // Parse Answers from the subsequent cells until the end of the row
                    while (cellIterator.hasNext()) {
                        Cell AnswerCell = cellIterator.next();
                        String AnswerText = getCellValue(AnswerCell).trim();

                        Answer Answer = new Answer(AnswerText);
                        Answer.setQuestion(question);
                        Answers.add(Answer);
                    }

                    // Find the matching Answer for the correct answer and mark it as correct
                    String answerText = getCellValue(row.getCell(2)).trim();
                    for (Answer Answer : Answers) {
                        if (Answer.getText().equalsIgnoreCase(answerText)) {
                            Answer.setIsCorrect(true);
                        } else {
                            Answer.setIsCorrect(false);
                        }
                    }

                    question.setAnswers(Answers);
                    questions.add(question);
                }

                quiz.setQuestions(questions);

                // Save the quiz object without saving the correct answer as a Answer
                quizUUIDRepo.save(quiz);
            }
        } catch (IOException | EncryptedDocumentException e) {
            log.error(null, e);
        }
    }

    // Method to get the cell value as a string, handling both string and numeric
    // cell types
    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Convert numeric value to string
                return String.valueOf(cell.getNumericCellValue());
            default:
                return ""; // Return empty string for other cell types
        }
    }

    public QuizUUID getQuizById(UUID id) {
        // Implement the logic to get quiz by its UUID from the repository
        // For example:
        return quizUUIDRepo.findById(id).orElse(null);
    }

    @Override
    public ByteArrayInputStream getDataDownloaded(QuizUUID quiz) throws IOException {
        String[] columns = { "Question", "Type", "Correct Answer", "Choice 1", "Choice 2", "Choice 3", "Choice 4",
                "Choice 5", "Choice 6" };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Quiz Data");
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

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
            for (Quest question : quiz.getQuestions()) {
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
                        correctCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                        correctCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
