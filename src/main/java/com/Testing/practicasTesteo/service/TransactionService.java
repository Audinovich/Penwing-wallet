package com.Testing.practicasTesteo.service;


import com.Testing.practicasTesteo.dto.ResponseDTO;
import com.Testing.practicasTesteo.dto.TransactionsDTO;
import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    List<Transactions> getAllTransactions() throws NotFoundException;

    List<Transactions> getTransactionsByDate(@RequestParam("transactionDate") LocalDateTime data);

    List<Transactions> getAllTransactionsByCustomerId(Long customerId);

    ResponseDTO saveTransaction(TransactionsDTO transactionsDTO);
}
