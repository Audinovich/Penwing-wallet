package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewCotroller {

    @Autowired
    CustomerService customerService;

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

    //validacion de datos con base de datos
    @PostMapping("/submit-login")
    public String login(@RequestParam("email") String name, @RequestParam("password") String password, Model model, HttpSession session) {

        System.out.println("Intento de login - nombre: " + name + " - contraseña: " + password);
        Customer authCustomer = customerService.authenticate(name, password);

        if (authCustomer!= null) {
            System.out.println("Ingresado CORRECTAMENTE - nombre: " + name + " - contraseña: " + password);
            System.out.println("ID del usuario: " + authCustomer.getCustomerId());
            session.setAttribute("usuario_id", authCustomer.getCustomerId()); //
            return "UserMenu";
        } else {
            System.out.println("INGRESO FALLIDO - nombre: " + name + " - contraseña: " + password);
            return "Index";
        }
    }
}