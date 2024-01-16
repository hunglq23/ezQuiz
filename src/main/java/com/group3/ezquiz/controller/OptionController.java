package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.service.IOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/options")
public class OptionController {

    private final IOptionService optionService;

    @Autowired
    public OptionController(IOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("")
    public String listOptions(Model model) {
        List<Option> options = optionService.getAllOptions();
        model.addAttribute("options", options);
        return "option";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("option", new Option());
        return "option/create";
    }

    @PostMapping("/create")
    public String createOption(@ModelAttribute("option") Option option) {
        optionService.createOption(option);
        return "redirect:/options";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Option option = optionService.getOptionById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        model.addAttribute("option", option);
        return "option/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOption(@PathVariable Integer id, @ModelAttribute("option") Option updatedOption) {
        optionService.updateOption(id, updatedOption);
        return "redirect:/options";
    }

    @GetMapping("/delete/{id}")
    public String deleteOption(@PathVariable Integer id) {
        optionService.deleteOption(id);
        return "redirect:/options";
    }
}
