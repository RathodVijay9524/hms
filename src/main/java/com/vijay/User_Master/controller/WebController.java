package com.vijay.User_Master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("uid") Long uid, @RequestParam("token") String token, Model model) {
        model.addAttribute("uid", uid);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @GetMapping("/verify-account")
    public String verifyAccount(@RequestParam("uid") Long uid, @RequestParam("code") String code, Model model) {
        model.addAttribute("uid", uid);
        model.addAttribute("code", code);
        return "verify-account";
    }

    // Home Page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Lab Management Pages
    @GetMapping("/lab/tests")
    public String labTests() {
        return "lab/tests";
    }

    @GetMapping("/lab/patients")
    public String labPatients() {
        return "lab/patients";
    }

    @GetMapping("/lab/orders")
    public String labOrders() {
        return "lab/orders";
    }
}
