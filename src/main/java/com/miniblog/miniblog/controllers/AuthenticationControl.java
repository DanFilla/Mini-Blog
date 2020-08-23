package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.models.data.UserRepository;
import com.miniblog.miniblog.models.dto.registerFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class
AuthenticationControl {

    @Autowired
    UserRepository userRepository;

    private static final String userSessionKey = "user";

    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(new registerFormDTO());
        model.addAttribute("title", "Register");
        return "register";
    }
}
