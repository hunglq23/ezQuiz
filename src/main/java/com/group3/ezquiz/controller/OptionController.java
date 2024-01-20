package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.service.impl.OptionServiceImpl;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/options")
public class OptionController {

    private final OptionServiceImpl optionService;
    private final OptionRepo optionRepo;

    @PostMapping("/edit/{id}")
    public String updateOption(@PathVariable Long id, @ModelAttribute("option") Option updatedOption) {
        // Your logic here to update the option
        // You might want to check if the associated question is not null before
        // updating

        optionService.updateOption(id, updatedOption);

        return "redirect:/questions"; // or wherever you want to redirect after the update
    }

    @GetMapping("/delete/{id}")
    public String deleteOption(@PathVariable Long id) {
        optionRepo.deleteById(id);
        return "redirect:/questions";
    }
}
