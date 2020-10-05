package com.miniblog.miniblog.controllers;

import com.miniblog.miniblog.MyUserDetails;
import com.miniblog.miniblog.models.Comment;
import com.miniblog.miniblog.models.Status;
import com.miniblog.miniblog.models.User;
import com.miniblog.miniblog.models.UserRelationship;
import com.miniblog.miniblog.models.data.CommentRepository;
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
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    StatusRepository statusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RelationshipRepository relationshipRepository;
    @Autowired
    CommentRepository commentRepository;

    private void fragmentAttributes(Model model, UserDetails userForUsername, ArrayList<User> friendsList) {
        model.addAttribute("title", "Mini-Blog");
        model.addAttribute("username", userForUsername.getUsername());
        model.addAttribute("friendsList", friendsList);
    }

    private boolean isFriend(int currentId, int possibleFriend) {
        if (currentId == possibleFriend) {
//            You are a friend to yourself :)
            return true;
        }

        Iterable<UserRelationship> relationshipList;

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentId < possibleFriend) {
            relationshipList = relationshipRepository.findAllByUserIdOneId(currentId);
        }else {
            relationshipList = relationshipRepository.findAllByUserIdTwoId(currentId);
        }

        for (UserRelationship relationship : relationshipList) {
            if (relationship.getUserIdOne().getId() == currentId
                && relationship.getUserIdTwo().getId() == possibleFriend
                && relationship.getStatusCode() == 1) {
                return true;
            }
        }

        return false;
    }

    private ArrayList<User> findAllFriends(int currentUserId) {

        ArrayList<User> friendsList = new ArrayList<User>();
        Iterable<UserRelationship> relationshipListIdOne = relationshipRepository.findAllByUserIdOneId(currentUserId);
        Iterable<UserRelationship> relationshipListIdTwo = relationshipRepository.findAllByUserIdTwoId(currentUserId);

        for (UserRelationship user : relationshipListIdOne) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdTwo());
            }
        }

        for (UserRelationship user : relationshipListIdTwo) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdOne());
            }
        }

        return friendsList;
    }


    @GetMapping("/")
    public String home(Model model) {
        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId());

        ArrayList<Status> statusList = new ArrayList<Status>();
        ArrayList<User> friendsList = findAllFriends(userDetails.getId());
        friendsList.add(user);

        for (User friend : friendsList) {
            statusList.addAll(statusRepository.findAllByUserId(friend.getId()));
        }

        fragmentAttributes(model, userDetails, friendsList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("allFriends", friendsList);
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

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        ArrayList<Status> statusList = statusRepository.findAllByUserId(userId);
        User user = userRepository.findById(userId);
        MyUserDetails userForUsername = new MyUserDetails(user);

        ArrayList<User> friendsList = findAllFriends(userId);
        fragmentAttributes(model, userForUsername, friendsList);

        model.addAttribute("isFriend", isFriend(currentUserId, userId));
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
//    friend status codes:
//        0: pending
//        1: accepted
//        2: declined
//        3: blocked

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        ArrayList<User> friendsList = new ArrayList<User>();
        ArrayList<User> pendingList = new ArrayList<User>();
        ArrayList<User> sentList = new ArrayList<User>();
        Iterable<UserRelationship> relationshipListIdOne = relationshipRepository.findAllByUserIdOneId(currentUserId);
        Iterable<UserRelationship> relationshipListIdTwo = relationshipRepository.findAllByUserIdTwoId(currentUserId);


        for (UserRelationship user : relationshipListIdOne) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdTwo());
            }else if (user.getStatusCode() == 0 && user.getActionUserId() != currentUserId) {
                pendingList.add(user.getUserIdTwo());
            }else if (user.getStatusCode() == 0 && user.getActionUserId() == currentUserId) {
                sentList.add(user.getUserIdTwo());
            }
        }

        for (UserRelationship user : relationshipListIdTwo) {
            if (user.getStatusCode() == 1) {
                friendsList.add(user.getUserIdOne());
            }else if (user.getStatusCode() == 0 && user.getActionUserId() != currentUserId) {
                pendingList.add(user.getUserIdOne());
            }else if (user.getStatusCode() == 0 && user.getActionUserId() == currentUserId) {
                sentList.add(user.getUserIdTwo());
            }
        }

        fragmentAttributes(model, userDetails, friendsList);

        model.addAttribute("friendsList", friendsList);
        model.addAttribute("pendingList", pendingList);
        model.addAttribute("sentList", sentList);

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

    @GetMapping("/friend/remove/{id}")
    public String processFriendRemoval(@PathVariable("id") int removalId, Model model, HttpSession session) {
        Iterable<UserRelationship> relationshipList;

        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        if (currentUserId < removalId) {
            relationshipList = relationshipRepository.findAllByUserIdTwoId(removalId);
        }else{
            relationshipList = relationshipRepository.findAllByUserIdOneId(removalId);
        }

        for (UserRelationship relationship : relationshipList) {
            if ((relationship.getUserIdOne().getId() == currentUserId && relationship.getUserIdTwo().getId() == removalId)
                    || (relationship.getUserIdOne().getId() == removalId && relationship.getUserIdTwo().getId() == currentUserId)) {
                relationshipRepository.delete(relationship);
                break;
            }
        }
        return "redirect:/friend";
    }

    @GetMapping("/search")
    public String processSearch(Model model, @RequestParam(value="q") String q) {
        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentUserId = userDetails.getId();

        ArrayList<User> matched = new ArrayList<User>();
        ArrayList<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (user.getUsername().contains(q)) {
                matched.add(user);
            }
        }
        ArrayList<User> friendsList = findAllFriends(userDetails.getId());

        fragmentAttributes(model, userDetails, friendsList);

        model.addAttribute("matched", matched);

        return "search";
    }

    @GetMapping("/status/{id}")
    public String displayStatusPage(@PathVariable("id") int statusId, Model model) {
        MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ArrayList<User> friendsList = findAllFriends(userDetails.getId());

        ArrayList<Comment> commentList = commentRepository.findAllByStatusId(statusId);


        Optional<Status> optStatus = statusRepository.findById(statusId);
        optStatus.ifPresent(status -> model.addAttribute("status", status));
        fragmentAttributes(model, userDetails, friendsList);
        model.addAttribute("commentList", commentList);
        model.addAttribute(new Comment());

        return "status";
    }

    @PostMapping("/comment/{id}")
    public String processComment(@ModelAttribute @Valid Comment newComment,
                                 @PathVariable("id") int statusId,
                                 Model model,
                                 Errors errors) {

        if (errors.hasErrors()) {
            return "index";
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        User currentUser = userRepository.findByUsername(username);

        Optional<Status> currentStatus = statusRepository.findById(statusId);
        currentStatus.ifPresent(stat -> newComment.setStatus(stat));
        currentStatus.ifPresent(stat -> newComment.setUser(currentUser));
        commentRepository.save(newComment);


        return "redirect:/status/" + statusId;
    }

}