package com.teamlixy.teamlixy.controller;

import com.teamlixy.teamlixy.model.User;
import com.teamlixy.teamlixy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);


    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        User user = userService.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, User updatedUser) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        userService.updateUserProfile(user);
        return "redirect:/profile";
    }

}
