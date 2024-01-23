package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.service.impl.OptionServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/options")
public class OptionController {

    private final OptionServiceImpl optionService;

    @PostMapping("/edit/{id}")
    public String updateOption(@PathVariable Long id, @ModelAttribute("option") Option updatedOption) {
        optionService.updateOption(id, updatedOption);
        return "redirect:/questions";
    }

}
