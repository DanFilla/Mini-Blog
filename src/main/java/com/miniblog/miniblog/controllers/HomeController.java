package com.miniblog.miniblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("")
    public String index(Model model) {

        model.addAttribute("title", "Mini-Blog");

        return "index";
    }
}
