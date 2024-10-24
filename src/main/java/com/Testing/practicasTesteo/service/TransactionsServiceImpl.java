package com.Testing.practicasTesteo.service;


import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.exceptions.TransactionNotFoundException;
import com.Testing.practicasTesteo.respository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionsServiceImpl implements TransactionService {

    @Autowired
    TransactionsRepository transactionsRepository;


    @Override
    public List<Transactions> getAllTransactions() throws TransactionNotFoundException {

        try {
            List<Transactions> transactionsList = transactionsRepository.findAll();
            if (transactionsList.isEmpty()) {
                throw new TransactionNotFoundException("Transactions Not Found");
            }
            return transactionsList;
        } catch (DataAccessException e) {
            throw new NotFoundException("Error accessing transaction data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transactions> getTransactionsByDate(LocalDateTime transactionDate) {

        try {
            List<Transactions> transactionsListByTransactionData = transactionsRepository.findTransactionsByTransactionDate(transactionDate);
            if (transactionsListByTransactionData.isEmpty()) {
                throw new TransactionNotFoundException("Transactions Not Found");
            }
            return transactionsListByTransactionData;
        } catch (DataAccessException e) {
            throw new NotFoundException("Error accessing transaction data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public Transactions saveTransaction(Transactions transaction) {
        try {
            return transactionsRepository.save(transaction);
        } catch (Exception e) {
            throw new NotSavedException("Error saving articles: " + e.getMessage(), e);
        }
    }
}
