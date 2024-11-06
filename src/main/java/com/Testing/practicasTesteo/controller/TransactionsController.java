package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.dto.ResponseDTO;
import com.Testing.practicasTesteo.dto.TransactionsDTO;
import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.exceptions.TransactionNotFoundException;
import com.Testing.practicasTesteo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionsController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/getAllTransactions")
    public ResponseEntity<List<Transactions>> getAllTransactions() {
        try {
            List<Transactions> transactionListFound = transactionService.getAllTransactions();
            return new ResponseEntity<>(transactionListFound, HttpStatus.OK);
        } catch (TransactionNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllTransactionsByTransactionDate/{transactionDate}")

    public ResponseEntity<List<Transactions>> getArticleById(@PathVariable("transactionDate") LocalDateTime transactionDate) {
        try {
            List<Transactions> transactionsFound = transactionService.getTransactionsByDate(transactionDate);
            return new ResponseEntity<>(transactionsFound, HttpStatus.OK);
        } catch (ArticleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/saveTransaction")
    public ResponseEntity<ResponseDTO> saveTransaction(@RequestBody TransactionsDTO transactionsDTO) {
        try {
            // Llama al servicio para guardar la transacción
            ResponseDTO savedTransaction = transactionService.saveTransaction(transactionsDTO);

            // Devuelve la transacción guardada con el ID y otros detalles actualizados
            return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
        } catch (NotSavedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
