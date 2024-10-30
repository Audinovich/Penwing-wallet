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
public class ViewCotroller {

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

    //validacion de datos con base de datos
    @PostMapping("/submit-login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {
        System.out.println("Intento de login - email: " + email + " - password: " + password);
        try {
            Customer authCustomer = customerService.authenticate(email, password);
            if (authCustomer != null) {
                System.out.println("Ingresado CORRECTAMENTE - email: " + email);

                // Obtener el ID del cliente y almacenarlo en la sesión
                Long customerId = authCustomer.getCustomerId();
                String customerName = authCustomer.getName();
                System.out.println("ID del usuario: " + customerId);
                session.setAttribute("customer_id", customerId);
                session.setAttribute("customer_name", customerName);
                System.out.println("Customer name: " + customerName);

                // Obtener el ID de la wallet del cliente si existe
                Wallet wallet = authCustomer.getWallet();
                if (wallet != null) {
                    Long walletId = wallet.getWalletId();
                    System.out.println("ID de la wallet: " + walletId);
                    session.setAttribute("wallet_id", walletId);
                } else {
                    System.out.println("El cliente no tiene una wallet asociada.");
                    model.addAttribute("error", "No tienes una wallet asociada. Contacta al soporte.");
                    return "Index"; // Puedes redirigir a una página de error si no tiene wallet.
                }

                return "UserMenu";  // Página a la que redirigir después del login exitoso

            } else {
                System.out.println("INGRESO FALLIDO - email: " + email);
                model.addAttribute("error", "Credenciales incorrectas. Inténtalo de nuevo.");
                return "Index";
            }
        } catch (AuthenticationException e) {
            System.out.println("Error de autenticación: " + e.getMessage());
            model.addAttribute("error", "Error de autenticación. Inténtalo de nuevo.");
            return "Index";
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            model.addAttribute("error", "Ha ocurrido un error inesperado. Por favor, intenta más tarde.");
            return "Index";
        }
    }
}