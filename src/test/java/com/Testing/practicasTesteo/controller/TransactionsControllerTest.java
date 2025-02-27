package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.dto.ResponseDTO;
import com.Testing.practicasTesteo.dto.TransactionsDTO;
import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.TransactionNotFoundException;
import com.Testing.practicasTesteo.service.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionsController.class)
class TransactionsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionsService transactionsService;

    private Transactions transaction;
    private List<Transactions> transactions;

    @BeforeEach
    void setUp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime transactionDate = LocalDateTime.parse("2024-11-07T13:15:48.451942", formatter);

        transaction = Transactions.builder()
                .transactionId(1L)
                .transactionDate(transactionDate)
                .description("Compra de 3 bitcoin")
                .amount(3.0)
                .creditType("bitcoin")
                .operation("add")
                .build();

        transactions = List.of(transaction);
    }

    @Test
    @DisplayName("GET /transaction/getAllTransactions - Should return 200 OK")
    void getAllTransactionsShouldReturnOK() throws Exception {
        when(transactionsService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/transaction/getAllTransactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].transactionId").value(1L))
                .andExpect(jsonPath("$.[0].transactionDate").value("2024-11-07T13:15:48.451942"))
                .andExpect(jsonPath("$.[0].description").value("Compra de 3 bitcoin"))
                .andExpect(jsonPath("$.[0].amount").value(3.0))
                .andExpect(jsonPath("$.[0].creditType").value("bitcoin"))
                .andExpect(jsonPath("$.[0].operation").value("add"));
    }

    @Test
    @DisplayName("GET /transaction/getAllTransactions - Should return 404 Not Found")
    void getAllTransactionsShouldReturnNotFound() throws Exception {
        when(transactionsService.getAllTransactions()).thenThrow(new TransactionNotFoundException("Transactions Not Found"));

        mockMvc.perform(get("/transaction/getAllTransactions"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transactions Not Found"));
    }

    @Test
    @DisplayName("GET /transaction/getAllTransactions - Should return 500 Internal Server Error")
    void getAllTransactionsShouldReturnInternalServerError() throws Exception {
        when(transactionsService.getAllTransactions()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/transaction/getAllTransactions"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @DisplayName("GET /transaction/getAllTransactionsByCustomerId - Should return 200 OK")
    void getAllTransactionsByCustomerIdShouldReturnOK() throws Exception {
        when(transactionsService.getAllTransactionsByCustomerId(1L)).thenReturn(transactions);

        mockMvc.perform(get("/transaction/getAllTransactionsByCustomerId/1").param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].transactionId").value(1L))
                .andExpect(jsonPath("$.[0].transactionDate").value("2024-11-07T13:15:48.451942"))
                .andExpect(jsonPath("$.[0].description").value("Compra de 3 bitcoin"))
                .andExpect(jsonPath("$.[0].amount").value(3.0))
                .andExpect(jsonPath("$.[0].creditType").value("bitcoin"))
                .andExpect(jsonPath("$.[0].operation").value("add"));
    }

    @Test
    @DisplayName("GET transaction/getAllTransactionsByCustomerId/1 - Should return 404 Not Found")
    void getAllTransactionsByCustomerIdShouldReturnNotFound() throws Exception {
        when(transactionsService.getAllTransactionsByCustomerId(1L)).thenThrow(new TransactionNotFoundException("Transactions Not Found"));

        mockMvc.perform(get("/transaction/getAllTransactionsByCustomerId/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transactions Not Found"));
    }

    @Test
    @DisplayName("GET /transaction/getAllTransactionsByCustomerId/1 - Should return 500 Internal Server Error")
    void getAllTransactionsByCustomerIdShouldReturnInternalServerError() throws Exception {
        when(transactionsService.getAllTransactionsByCustomerId(1L)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/transaction/getAllTransactionsByCustomerId/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @DisplayName("POST /transaction/saveTransaction - Should return 201 Created")
    void saveTransactionShouldReturnCreated() throws Exception {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTransactionId(1L);
        responseDTO.setTransactionDate(LocalDateTime.parse("2024-11-07T13:15:48.451942", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")));
        responseDTO.setDescription("Compra de 3 bitcoin");
        responseDTO.setAmount(3.0);
        responseDTO.setCreditType("bitcoin");
        responseDTO.setOperation("add");

        when(transactionsService.saveTransaction(any(TransactionsDTO.class))).thenReturn(responseDTO);

        String transactionJson = """
                {
                    "customerId": 1,
                    "amount": 3.0,
                    "creditType": "bitcoin",
                    "operation": "add"
                }
                """;

        mockMvc.perform(post("/transaction/saveTransaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(1L))
                .andExpect(jsonPath("$.transactionDate").value("2024-11-07T13:15:48.451942"))
                .andExpect(jsonPath("$.description").value("Compra de 3 bitcoin"))
                .andExpect(jsonPath("$.amount").value(3.0))
                .andExpect(jsonPath("$.creditType").value("bitcoin"))
                .andExpect(jsonPath("$.operation").value("add"));
    }
}
