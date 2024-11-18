package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/credit")
public class CreditController {

    @Autowired
    CreditService creditService;

    @GetMapping("/getAllCreditsByCustomerId/{customerId}")
    public ResponseEntity<?> getAllCreditsByCustomerId(@PathVariable("customerId") long customerId)throws CustomerNotFoundException {
        try {
            List<Credit> creditsFound = creditService.getAllCreditsByCustomerId(customerId);
            return new ResponseEntity<>(creditsFound, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/addEuroCredit/{customerId}")
    public ResponseEntity<Credit> addCredit(@PathVariable Long customerId, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            Credit updatedCredit = creditService.addEuroCredit(customerId, amount);
            return new ResponseEntity<>(updatedCredit, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/articleCreditInfo/{customerId}")
    public ResponseEntity<List<ArticleCreditDTO>> getArticleCreditInfo(@PathVariable("customerId") Long customerId) {
        try {
            List<ArticleCreditDTO> articleCreditInfo = creditService.getArticleCreditInfoByCustomerId(customerId);
            return ResponseEntity.ok(articleCreditInfo);
        } catch (ArticleFetchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updateCryptoBalance/{customerId}")
    public ResponseEntity<?> updateCryptoBalance(@PathVariable Long customerId, @RequestBody Map<String, Object> request)throws NotFoundException {
        try {
            String creditType = (String) request.get("creditType");
            Double amount = ((Number) request.get("amount")).doubleValue();
            String operation = (String) request.get("operation");

            Credit updatedCredit = creditService.updateCryptoBalance(customerId, amount, creditType, operation);
            return new ResponseEntity<>(updatedCredit, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
