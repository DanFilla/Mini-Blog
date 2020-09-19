package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.models.Status;
import com.miniblog.miniblog.models.User;
import com.miniblog.miniblog.models.data.StatusRepository;
import com.miniblog.miniblog.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/")
    public String home(Model model) {
        Iterable statusList = statusRepository.findAll();

        model.addAttribute("title", "Mini-Blog");
        model.addAttribute("statusList", statusList);
        model.addAttribute(new Status());

        return "index";
    }

    @PostMapping("/post")
    public String post(@ModelAttribute @Valid Status newStatus, Errors errors, Model model, HttpSession session) {

        if (errors.hasErrors()) {
            return "index";
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        User currentUser = userRepository.findByUsername(username);
        newStatus.setUser(currentUser);

        statusRepository.save(newStatus);

        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String renderProfilePage(Model model, @PathVariable("id") int userId) {
        Iterable statusList = statusRepository.findAllByUserId(userId);

        model.addAttribute("statusList", statusList);
        model.addAttribute("title", "Mini-Blog");

        return "profile";
    }
}