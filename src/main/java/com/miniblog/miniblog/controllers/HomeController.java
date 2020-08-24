package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.models.Status;
import com.miniblog.miniblog.models.data.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    StatusRepository statusRepository;


    @GetMapping("/")
    public String home(Model model) {
        Iterable statusList = statusRepository.findAll();

        model.addAttribute("title", "Mini-Blog");
        model.addAttribute("statusList", statusList);

        return "index";
    }

    @GetMapping("/post")
    public String post(Model model) {
        model.addAttribute(new Status());
        return "post";
    }

    @PostMapping("/post")
    public String post(@ModelAttribute @Valid Status newStatus, Errors errors, Model model) {

        if (errors.hasErrors()) {
            return "index";
        }

        statusRepository.save(newStatus);

        return "redirect:/";
    }
}
