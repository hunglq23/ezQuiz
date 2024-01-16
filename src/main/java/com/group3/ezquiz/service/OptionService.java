package com.group3.ezquiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.repository.OptionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService implements IOptionService {

    @Autowired
    private OptionRepository optionRepository;

    @Override
    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    @Override
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    @Override
    public Optional<Option> getOptionById(Integer id) {
        return optionRepository.findById(id);
    }

    @Override
    public Option updateOption(Integer id, Option updatedOption) {
        Option existingOption = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        // Update fields of existingOption with values from updatedOption
        existingOption.setContent(updatedOption.getContent());
        existingOption.setAnswer(updatedOption.isAnswer());

        // Save the updated option
        return optionRepository.save(existingOption);
    }

    @Override
    public void deleteOption(Integer id) {
        optionRepository.deleteById(id);
    }
}
