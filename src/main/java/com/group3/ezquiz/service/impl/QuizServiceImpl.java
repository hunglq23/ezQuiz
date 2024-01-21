package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.repository.QuizRepository;
import com.group3.ezquiz.service.QuizService;
import com.group3.ezquiz.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserService userService;

    @Override
    public List<Quiz> listAll() {
        return (List<Quiz>) quizRepository.findAll() ;

    }
    @Override
    public void createQuiz(HttpServletRequest request, QuizDto quizDto) {

        quizRepository.save(
                Quiz.builder()
                        .code(quizDto.getCode())
                        .title(quizDto.getTitle())
                        .description(quizDto.getDescription())
                        .isExamOnly(quizDto.getIsExamOnly())
                        .isAcitve(quizDto.getIsActive())
                        .createdBy(userService.getUserRequesting(request))
                        .build());
    }

    @Override
    public Optional<Quiz> findQuizById(Integer id) {
        return quizRepository.findById(id);
    }

    @Override
    public Quiz updateQuiz(Integer id, Quiz quiz) throws ChangeSetPersister.NotFoundException {
        Optional<Quiz> optionalQuiz = quizRepository.findById(id);
        if(optionalQuiz.isPresent()){
            Quiz existedQuiz = optionalQuiz.get();

            existedQuiz.setCode(quiz.getCode());
            existedQuiz.setTitle(quiz.getTitle());
            existedQuiz.setDescription(quiz.getDescription());
            existedQuiz.setIsExamOnly(quiz.getIsExamOnly());
            existedQuiz.setIsAcitve(quiz.getIsAcitve());
            existedQuiz.setUpdateAt(quiz.getUpdateAt());

            return quizRepository.save(existedQuiz);
        } else {
            throw new ResourceNotFoundException("Quiz with id "+ id +" not found!");
        }
    }

    @Override
    public void deleteQuiz(Integer id) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(id);
        if(optionalQuiz.isPresent()){
            Quiz existedQuiz = optionalQuiz.get();
            quizRepository.delete(existedQuiz);
        } else {
            throw new ResourceNotFoundException("Quiz with id "+ id +"not found!");
        }
    }

    @Override
    public Page<Quiz> paginated(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return quizRepository.findAll(pageable);
    }


    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
