package com.Testing.practicasTesteo.service;


import com.Testing.practicasTesteo.dto.CustomerDTO;
import com.Testing.practicasTesteo.dto.ResponseDTO;
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
    public List<Transactions> getAllTransactionsByCustomerId(Long customerId) throws TransactionNotFoundException {
        try {
            List<Transactions> transactionsList = transactionsRepository.findTransactionsByCustomerId(customerId);
            if (transactionsList.isEmpty()) {
                throw new TransactionNotFoundException("Not transaction found whit Customer Id" + customerId);
            } else {
                return transactionsList;
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);

        }

    }


    @Override
    public ResponseDTO saveTransaction(TransactionsDTO transactionDTO) {
        try {
            // Cargar el cliente desde la base de datos usando el customerId del DTO
            Customer customer = customerRepository.findById(transactionDTO.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found"));

            // Establecer la fecha de la transacción
            LocalDateTime transactionDate = LocalDateTime.now();

            // Generar la descripción
            String description = transactionDTO.getOperation().equals("buy")
                    ? "Compra de " + transactionDTO.getAmount() + " " + transactionDTO.getCreditType()
                    : "Venta de " + transactionDTO.getAmount() + transactionDTO.getCreditType();

            // Crear la transacción
            Transactions transaction = Transactions.builder()
                    .transactionDate(transactionDate)
                    .description(description)
                    .amount(transactionDTO.getAmount())
                    .creditType(transactionDTO.getCreditType())
                    .operation(transactionDTO.getOperation())
                    .customer(customer)
                    .build();

            // Guardar la transacción
            Transactions savedTransaction = transactionsRepository.save(transaction);

            // Crear la respuesta
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setTransactionId(savedTransaction.getTransactionId());
            responseDTO.setTransactionDate(savedTransaction.getTransactionDate());
            responseDTO.setDescription(savedTransaction.getDescription());
            responseDTO.setAmount(savedTransaction.getAmount());
            responseDTO.setCreditType(savedTransaction.getCreditType());
            responseDTO.setOperation(savedTransaction.getOperation());

            // Inyectar solo el customerId
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(savedTransaction.getCustomer().getCustomerId());
            responseDTO.setCustomer(customerDTO);

            return responseDTO;

        } catch (DataAccessException e) {
            throw new NotSavedException("Error saving transaction: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

}

