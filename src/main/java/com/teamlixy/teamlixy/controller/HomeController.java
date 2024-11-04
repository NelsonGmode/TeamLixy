package com.teamlixy.teamlixy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Returns the login page
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Implement login logic here, such as checking credentials
        // If authentication is successful, redirect to /home
        return "redirect:/home";  // Redirect to home after successful login
    }

}
