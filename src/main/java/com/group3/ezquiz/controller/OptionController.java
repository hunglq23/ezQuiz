package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.service.impl.OptionServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/options")
public class OptionController {

    private final OptionServiceImpl optionService;

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Option option = optionService.getOptionById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        model.addAttribute("option", option);
        return "options/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOption(@PathVariable Long id,
            @ModelAttribute("option") Option updatedOption) {
        optionService.updateOption(id, updatedOption);
        return "redirect:/questions";
    }

    @GetMapping("/delete/{id}")
    public String deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return "redirect:/options";
    }
}
