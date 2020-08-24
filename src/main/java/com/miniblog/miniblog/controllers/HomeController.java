package com.miniblog.miniblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "You need to be logged in";
    }

    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("title", "Mini-Blog");

        return "index";
    }
}
