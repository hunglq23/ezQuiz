package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Answer;
import com.group3.ezquiz.model.Quest;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.ObjectDto;
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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public QuizUUID getQuizForQuizTaking(UUID id) {
        QuizUUID quizById = quizUUIDRepo.findQuizUUIDById(id);
        if (quizById != null) {
            return quizById;
        }
        throw new ResourceNotFoundException("Cannot find quiz with ID: " + id);
    }

    @Override
    public List<QuizDto> getQuizByCreator(HttpServletRequest request, Boolean sortOrder) {
        User userRequesting = getUserRequesting(request);
        List<QuizUUID> quizByCreator = quizUUIDRepo.findByCreator(userRequesting);
        List<QuizDto> quizDtoList = new ArrayList<>();
        for (QuizUUID quiz : quizByCreator) {
            QuizDto quizDto = QuizDto.builder()
                    .type("Quiz")
                    .title(quiz.getTitle())
                    .description(quiz.getDescription())
                    .image(quiz.getImageUrl())
                    .isDraft(quiz.getIsDraft())
                    .itemNumber(quiz.getQuestions().size())
                    .timeString(quiz.getCreatedAt().toString())
                    .build();
            quizDtoList.add(quizDto);
        }
        Comparator<QuizDto> comparator = Comparator.comparing(QuizDto::getTimeString);
        if (sortOrder) {
            comparator = comparator.reversed();
        }
        quizDtoList.sort(comparator);
        quizDtoList.forEach(objectDto -> objectDto.setTimeString(
                calculateTimeElapsed(
                        convertStringToTimestamp(objectDto.getTimeString(), "yyyy-MM-dd HH:mm:ss.SSSSSS"))));
        return quizDtoList;
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
    public List<QuizUUID> getListQuizUUID(HttpServletRequest request) {
        List<QuizUUID> data = quizUUIDRepo.findQuizUUID();
        return data;
    }

    @Override
    public List<QuizUUID> searchQuizUUID(HttpServletRequest request, String search) {
        List<QuizUUID> data = quizUUIDRepo.searchQuizUUID(search);
        return data;
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
    public void importQuizDataFromExcel(HttpServletRequest request, MultipartFile file, UUID id) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the quiz data is in the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Check if there is at least one row
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
                rowIterator.next();

                QuizUUID quiz = getQuizByRequestAndUUID(request, id);

                List<Quest> questions = new ArrayList<>();

                // Iterate over each row in the sheet starting from the third row
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    Quest question = new Quest();
                    question.setQuiz(List.of(quiz));

                    // Parse question text from the first cell of the row
                    Cell questionCell = cellIterator.next();
                    String questionText = getCellValue(questionCell).trim();
                    question.setText(questionText);

                    // Parse question type from the second cell of the row
                    Cell typeCell = cellIterator.next();
                    String questionType = getCellValue(typeCell).trim();
                    question.setType(questionType);

                    List<Answer> answers = new ArrayList<>();

                    // Skip the cell containing the correct answer(s)
                    cellIterator.next();

                    // Parse Answers from the subsequent cells until the end of the row
                    while (cellIterator.hasNext()) {
                        Cell answerCell = cellIterator.next();
                        String answerText = getCellValue(answerCell).trim();

                        Answer answer = new Answer(answerText);
                        answer.setQuestion(question);
                        answers.add(answer);
                    }

                    // For single choice questions, find the matching Answer for the correct answer
                    // and mark it as correct
                    if (questionType.equalsIgnoreCase("single choice")) {
                        String correctAnswerText = getCellValue(row.getCell(2)).trim();
                        for (Answer answer : answers) {
                            if (answer.getText().equalsIgnoreCase(correctAnswerText)) {
                                answer.setIsCorrect(true);
                            }
                        }
                    }
                    // For multiple choice questions, parse the correct answer indices and mark the
                    // corresponding Answers as correct
                    else if (questionType.equalsIgnoreCase("multiple choice")) {
                        String correctAnswerText = getCellValue(row.getCell(2)).trim();
                        List<Integer> correctAnswerIndexes = parseCorrectAnswers(correctAnswerText);
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

                quizUUIDRepo.save(quiz);

            }
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
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
                try {
                    correctAnswers.add(Integer.parseInt(index.trim()) - 1);
                } catch (NumberFormatException e) {
                    // Handle invalid index
                }
            }
        }
        return correctAnswers;
    }

    public QuizUUID getQuizById(UUID id) {
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

    public static Timestamp convertStringToTimestamp(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return Timestamp.valueOf(dateTime);
    }
    public static String calculateTimeElapsed(Timestamp creationTime) {
        Instant instant = creationTime.toInstant();
        Instant currentInstant = Instant.now();
        Duration duration = Duration.between(instant, currentInstant);

        long seconds = duration.getSeconds();

        long days = seconds / (24 * 3600);
        seconds = seconds % (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder timeElapsedStringBuilder = new StringBuilder();
        if (days > 0) {
            return days + " day(s) ago";
        }
        if (hours > 0) {
            return hours + " hour(s) ago";
        }
        if (minutes > 0) {
            return minutes + " minute(s) ago";
        }
        return seconds + " second(s) ago";
    }
}
