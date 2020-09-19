package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.models.User;
import com.miniblog.miniblog.models.data.UserRepository;
import com.miniblog.miniblog.models.dto.loginFormDTO;
import com.miniblog.miniblog.models.dto.registerFormDTO;
//import jdk.vm.ci.code.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class
AuthenticationControl {

    @Autowired
    UserRepository userRepository;


    private static final String userSessionKey = "user";

    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user.getId());
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {

        return "login";
    }

    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(new registerFormDTO());
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute @Valid registerFormDTO RegisterFormDTO, Errors errors,
                                      HttpServletRequest request, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Register");
            return "register";
        }
        User existingUser = userRepository.findByUsername(RegisterFormDTO.getUsername());

        if (existingUser != null) {
            errors.rejectValue("username", "username.alreadyexists", "That username already exists.");
            model.addAttribute("title", "Register");
            setUserInSession(request.getSession(), existingUser);
            return "register";
        }

        String password = RegisterFormDTO.getPassword();
        String verifyPassword = RegisterFormDTO.getVerifyPassword();
        if (!password.equals(verifyPassword)) {
            errors.rejectValue("password", "password.mismatch", "Your passwords do not match.");
            model.addAttribute("title", "Register");
            return "register";
        }

        User newUser = new User(RegisterFormDTO.getUsername(), RegisterFormDTO.getPassword());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRepository.save(newUser);

        try {
            request.login(RegisterFormDTO.getUsername(), RegisterFormDTO.getPassword());
        } catch (ServletException e) {
            errors.rejectValue("login", "no.login", "Could not perform login");
        }

        return "/index";
    }


}
