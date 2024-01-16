package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Option;

import java.util.List;
import java.util.Optional;

public interface IOptionService {

    Option createOption(Option option);

    List<Option> getAllOptions();

    Optional<Option> getOptionById(Integer id);

    Option updateOption(Integer id, Option updatedOption);

    void deleteOption(Integer id);
}
