package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.AuthenticationException;
import com.Testing.practicasTesteo.service.CustomerService;
import com.Testing.practicasTesteo.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @Autowired
    CustomerService customerService;
    @Autowired
    WalletService walletService;

    @GetMapping("/CustomerRegistration.html")
    public String showRegisterPage() {
        return "CustomerRegistration"; //
    }

    @GetMapping("/user-credit")
    public String getUserCredit(Model model, HttpSession session) {
        Long walletId = (Long) session.getAttribute("wallet_id");
        if (walletId != null) {

            Wallet wallet = walletService.getWalletById(walletId);

            model.addAttribute("wallet", wallet);
            model.addAttribute("customerName", session.getAttribute("customer_name"));
            return "UserCredit"; // Devuelve la vista del Credito
        } else {
            model.addAttribute("error", "No hay una wallet asociada a este usuario.");
            return "Index"; // O redirigir a otra página en caso de error
        }
    }


    @GetMapping("/user-wallet")
    public String getUserWallet(Model model, HttpSession session) {
        Long walletId = (Long) session.getAttribute("wallet_id");
        if (walletId != null) {

            Wallet wallet = walletService.getWalletById(walletId);

            model.addAttribute("wallet", wallet);
            model.addAttribute("customerName", session.getAttribute("customer_name")); // Ejemplo de cómo obtener el nombre del cliente
            return "UserWallet";
        } else {
            model.addAttribute("error", "No hay una wallet asociada a este usuario.");
            return "Index";
        }
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

    @RequestMapping("/UserMenu.html")
    public String userMenu() {
        return "UserMenu";
    }

    //validacion de datos con base de datos
    @PostMapping("/submit-login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {

        try {
            //devuelve el objeto de tipo customer validado , y puedo acceder a la info.
            Customer authCustomer = customerService.authenticate(email, password);
            if (authCustomer != null) {

                Long customerId = authCustomer.getCustomerId();
                String customerName = authCustomer.getName();
                session.setAttribute("customer_id", customerId);
                session.setAttribute("customer_name", customerName);


                Wallet wallet = authCustomer.getWallet();
                if (wallet != null) {
                    Long walletId = wallet.getWalletId();
                    session.setAttribute("wallet_id", walletId);
                } else {
                    model.addAttribute("error", "The customer has not a wallet");
                    return "Index";
                }
                return "UserMenu";

            } else {
                model.addAttribute("error", "Credenciales incorrectas. Inténtalo de nuevo.");
                return "Index";
            }
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Error de autenticación. Inténtalo de nuevo.");
            return "Index";
        } catch (Exception e) {
            model.addAttribute("error", "Ha ocurrido un error inesperado. Por favor, intenta más tarde.");
            return "Index";
        }
    }
}