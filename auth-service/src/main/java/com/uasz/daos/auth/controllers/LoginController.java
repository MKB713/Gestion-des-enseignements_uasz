package com.uasz.daos.auth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String index() {
        return "login";
    }

    @GetMapping("/auth2")
    public String auth2() {
        // Cette endpoint est utilisé par Spring Security pour le traitement du login
        return "redirect:/";
    }
}