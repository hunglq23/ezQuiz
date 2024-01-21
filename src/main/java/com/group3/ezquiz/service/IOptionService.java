package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Option;

import java.util.List;
import java.util.Optional;

public interface IOptionService {

    List<Option> getAllOptions();

    Optional<Option> getOptionById(Long id);

    Option updateOption(Long id, Option updatedOption);

    void deleteOption(Long id);
}
