package com.teamlixy.teamlixy.controller;

import com.teamlixy.teamlixy.model.User;
import com.teamlixy.teamlixy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String username) {

        // Find the user by username
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate a temporary password (you can also use a more secure method)
        String tempPassword = UUID.randomUUID().toString(); // Generates a random UUID as temp password
        user.setPassword(passwordEncoder.encode(tempPassword)); // Ensure you encode the password

        // Save the user with the new password
        userService.saveUser(user);

        // Log the temporary password to the console
        System.out.println("Temporary password for " + username + ": " + tempPassword);

        // Return the temporary password as part of the response
        return ResponseEntity.ok("Temporary password generated: " + tempPassword);

    }
}
