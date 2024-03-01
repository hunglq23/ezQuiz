package com.group3.ezquiz.service.impl;


import java.util.Optional;
import org.springframework.stereotype.Service;
import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.service.IOptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements IOptionService {

    private final OptionRepo optionRepository;

    @Override
    public Optional<Option> getOptionById(Long id) {
        return optionRepository.findById(id);
    }

    @Override
    public Option updateOption(Long id, Option updatedOption) {
        Optional<Option> optional = optionRepository.findById(id);
        if (optional.isPresent()) {
            Option existing = optional.get();
            // Update the option fields with the new data
            existing.setText(updatedOption.getText());
            // Save the updated option using the optionRepository
            Option updated = optionRepository.save(existing);
            // Log the update for debugging
            System.out.println("Option with ID " + id + " updated: " + updated);
            // Return the updated option
            return updated;
        } else {
            // Throw an exception if the option with the given ID is not found
            throw new ResourceNotFoundException("Option with ID " + id + " not found");
        }
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

}
