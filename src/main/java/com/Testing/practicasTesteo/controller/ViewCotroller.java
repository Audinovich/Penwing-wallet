package com.Testing.practicasTesteo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewCotroller {

    @GetMapping("/CustomerRegistration.html")
    public String showRegisterPage() {
        return "CustomerRegistration"; //
    }


    @GetMapping("/Alta.html")
    public String showAlta() {
        return "Alta";
    }

    @RequestMapping("/Index.html")
    public String index() {
        return "Index";
    }

    @RequestMapping("/Login.html")
    public String login() {
        return "Login";
    }
}