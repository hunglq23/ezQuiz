package com.group3.ezquiz.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.repository.QuestionRepo;
import com.group3.ezquiz.service.IOptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements IOptionService {

    private final OptionRepo optionRepository;
    private final QuestionRepo questionRepository;

    @Override
    public Optional<Option> getOptionById(Long id) {
        return optionRepository.findById(id);
    }

    @Override
    public Option updateOption(Long id, Option updatedOption) {
        // Step 1: Check if the option with the given ID exists
        Optional<Option> optionalOption = optionRepository.findById(id);

        if (optionalOption.isPresent()) {
            // Step 2: Retrieve the existing option
            Option existingOption = optionalOption.get();

            // Step 3: Update the existing option with the new data
            existingOption.setText(updatedOption.getText());
            existingOption.setAnswer(updatedOption.isAnswer());

            // Step 4: Check and update the question only if it is not null in the request
            if (updatedOption.getQuestion() != null) {
                // Fetch the associated Question from the database
                Question associatedQuestion = questionRepository.findById(updatedOption.getQuestion().getQuestionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

                // Update the Question association
                existingOption.setQuestion(associatedQuestion);
            }

            // Step 5: Update other fields as needed

            // Step 6: Save the updated option using the repository
            Option updatedOptionEntity = optionRepository.save(existingOption);

            // Step 7: Log the update for debugging
            System.out.println("Option with ID " + id + " updated: " + updatedOptionEntity);

            // Step 8: Return the updated option
            return updatedOptionEntity;
        } else {
            // Step 9: Throw an exception if the option with the given ID is not found
            throw new ResourceNotFoundException("Option with ID " + id + " not found");
        }
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @Override
    public void deleteOption(Long id) {
        Optional<Option> option = optionRepository.findById(id);

        if (option.isPresent()) {
            Option option1 = option.get();
            optionRepository.delete(option1);
        } else {
            // Handle the case where the question does not exist
            // You might throw an exception or log a message
        }
    }

    @Override
    public List<Option> getAllOptions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllOptions'");
    }
}
