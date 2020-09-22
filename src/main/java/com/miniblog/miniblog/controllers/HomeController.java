package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.MyUserDetails;
import com.miniblog.miniblog.models.Status;
import com.miniblog.miniblog.models.User;
import com.miniblog.miniblog.models.UserRelationship;
import com.miniblog.miniblog.models.data.RelationshipRepository;
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
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HomeController {

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RelationshipRepository relationshipRepository;

    private void fragmentAttributes(Model model, UserDetails userForUsername) {

        model.addAttribute("title", "Mini-Blog");
        model.addAttribute("username", userForUsername.getUsername());
    }

    @GetMapping("/")
    public String home(Model model) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        fragmentAttributes(model, userDetails);

        Iterable statusList = statusRepository.findAll();

        model.addAttribute("statusList", statusList);
        model.addAttribute("userDetails", userDetails);
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

        Iterable<Status> statusList = statusRepository.findAllByUserId(userId);

        MyUserDetails userForUsername = new MyUserDetails(statusList.iterator().next().getUser());
        fragmentAttributes(model, userForUsername);

        model.addAttribute("statusList", statusList);

        return "profile";
    }

    @PostMapping("/friend/{id}")
    public String processFriendRequest(@ModelAttribute @Valid UserRelationship userRelationship,
                                       @PathVariable("id") int userRequestedId,
                                       Model model,
                                       Errors errors) {

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();
        User smallerId;
        User largerId;

        if (userRequestedId < currentUserId) {
            smallerId = userRepository.findById(userRequestedId);
            largerId = userRepository.findById(currentUserId);
        }else {
            smallerId = userRepository.findById(currentUserId);
            largerId = userRepository.findById(userRequestedId);
        }

        int largeId = largerId.getId();
        Iterable<UserRelationship> userOneList = relationshipRepository.findAllByUserIdOneId(smallerId.getId());
        for (UserRelationship relationship : userOneList) {
            if (relationship.getUserIdTwo().getId() == largeId) {
                return "redirect:/";
            }
        }

        UserRelationship relationship = new UserRelationship(smallerId, largerId, 0, currentUserId);

        relationshipRepository.save(relationship);

        return "redirect:/";
    }

    @GetMapping("/friend")
    public String friendsPage(Model model) {

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        ArrayList<User> friendsList = new ArrayList<User>();
        ArrayList<User> pendingList = new ArrayList<User>();
        Iterable<UserRelationship> relationshipListIdOne = relationshipRepository.findAllByUserIdOneId(currentUserId);
        Iterable<UserRelationship> relationshipListIdTwo = relationshipRepository.findAllByUserIdTwoId(currentUserId);


        for (UserRelationship user : relationshipListIdOne) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdTwo());
            }else if (user.getStatusCode() == 0) {
                pendingList.add(user.getUserIdTwo());
            }
        }

        for (UserRelationship user : relationshipListIdTwo) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdOne());
            }else if (user.getStatusCode() == 0) {
                pendingList.add(user.getUserIdOne());
            }
        }

        model.addAttribute("friendsList", friendsList);
        model.addAttribute("pendingList", pendingList);

        return "friendsPage";
    }

    @GetMapping("/friend/add/{id}")
    public String processFriendAccept(@PathVariable("id") int requestedUserId, Model model, HttpSession session) {
        Iterable<UserRelationship> relationshipList;

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        if (currentUserId < requestedUserId) {
            relationshipList = relationshipRepository.findAllByUserIdTwoId(requestedUserId);
        }else{
            relationshipList = relationshipRepository.findAllByUserIdOneId(requestedUserId);
        }

        for (UserRelationship relationship : relationshipList) {
            if ((relationship.getUserIdOne().getId() == currentUserId && relationship.getUserIdTwo().getId() == requestedUserId)
                || (relationship.getUserIdOne().getId() == requestedUserId && relationship.getUserIdTwo().getId() == currentUserId)) {
                relationship.setStatusCode(1);
                relationshipRepository.save(relationship);
                break;
            }
        }

        return "redirect:/friend";
    }


}