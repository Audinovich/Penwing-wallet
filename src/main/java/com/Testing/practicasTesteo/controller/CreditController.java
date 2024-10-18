package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/credit")
public class CreditController {

    @Autowired
    CreditService creditService;

    @GetMapping("/getCreditById/{customerId}")
    public ResponseEntity<List<Credit>> getAllCreditsByCustomerId(@PathVariable("customerId") Long customerId) throws ArticleFetchException {
        try {
            List<Credit> creditsFound = creditService.getAllCreditsByCustomerId(customerId);
            return new ResponseEntity<>(creditsFound, HttpStatus.OK);

        } catch (ArticleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/addCredit/{customerId}")
    public ResponseEntity<Credit> addCredit(@PathVariable Long customerId, @RequestBody Map<String, Long> request) {
        try {
            Long amount = request.get("amount");
            Credit updatedCredit = creditService.addCredit(customerId, amount);
            return new ResponseEntity<>(updatedCredit, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
