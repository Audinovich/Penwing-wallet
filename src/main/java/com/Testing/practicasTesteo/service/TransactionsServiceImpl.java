package com.Testing.practicasTesteo.service;


import com.Testing.practicasTesteo.dto.TransactionsDTO;
import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.exceptions.TransactionNotFoundException;
import com.Testing.practicasTesteo.respository.CustomerRepository;
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

    @Autowired
    CustomerRepository customerRepository;


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
    public List<Transactions> getAllTransactionsByCustomerId(Long customerId) {
        return List.of();
    }


    @Override
    public Transactions saveTransaction(TransactionsDTO transactionDTO) {
        try {
            // Cargar el cliente desde la base de datos usando el customerId del DTO
            Customer customer = customerRepository.findById(transactionDTO.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found"));

            // Establecer la fecha de la transacción si no se proporciona
            LocalDateTime transactionDate = LocalDateTime.now(); // Establece la fecha y hora actuales

            // Generar la descripción de la transacción si no se proporciona
            String description = transactionDTO.getOperation().equals("buy")
                    ? "Compra de " + transactionDTO.getCreditType() + " por " + transactionDTO.getAmount() + " euros."
                    : "Venta de " + transactionDTO.getCreditType() + " por " + transactionDTO.getAmount() + " euros.";

            // Crear la transacción con los datos del DTO
            Transactions transaction = Transactions.builder()
                    .transactionDate(transactionDate) // Establece la fecha y hora actuales
                    .description(description)          // Asigna la descripción generada
                    .amount(transactionDTO.getAmount()) // Asigna el monto de la transacción
                    .creditType(transactionDTO.getCreditType()) // Asigna el tipo de crédito
                    .operation(transactionDTO.getOperation()) // Asigna la operación (compra/venta)
                    .customer(customer)                // Asigna el cliente cargado
                    .build();

            // Guardar la transacción en la base de datos
            return transactionsRepository.save(transaction);

        } catch (DataAccessException e) {
            throw new NotSavedException("Error saving transaction: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

}

