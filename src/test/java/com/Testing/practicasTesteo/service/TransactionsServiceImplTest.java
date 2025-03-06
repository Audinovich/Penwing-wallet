package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Transactions;
import com.Testing.practicasTesteo.exceptions.TransactionNotFoundException;
import com.Testing.practicasTesteo.respository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceImplTest {

    //TODO funcion de inject mocks
    @InjectMocks
    TransactionService transactionsService;

    @Mock
    TransactionsRepository transactionsRepository;

    //TransactionService transactionsService ;

    private Transactions transaction;

    @BeforeEach
    void setUp() {

        transaction = Transactions.builder()
                .transactionId(1L)
                .description("Compra de Bitcoin")
                .creditType("bitcoin")
                .operation("buy")
                .build();

        //transactionsService = new TransactionsServiceImpl(transactionsRepository);
    }


    @Test
    void getAllTransactions_ShouldReturnListOfTransactions_WhenTransactionsExist() {
        when(transactionsRepository.findAll()).thenReturn(List.of(transaction));

        List<Transactions> transactionsFound = transactionsRepository.findAll();

        assertNotNull(transactionsFound);
        assertEquals(1, transactionsFound.size());
        assertEquals("bitcoin", transactionsFound.get(0).getCreditType());
    }


    @Test
    void getAllArticles_ShouldThrowTransactionNotFoundException_WhenFindAll() {

        when(transactionsRepository.findAll()).thenThrow(new TransactionNotFoundException("Transactions Not Found") {
        });

        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            transactionsService.getAllTransactions();
        });

        assertEquals("Transactions Not Found", exception.getMessage());

        verify(transactionsRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_ShouldThrowDataAccessException_WhenFindAll() {

        when(transactionsRepository.findAll()).thenThrow(new DataAccessException("Data Error") {
        });

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionsService.getAllTransactions();
        });

        assertEquals("Error accessing transaction data: Data Error", exception.getMessage());

        verify(transactionsRepository, times(1)).findAll();
    }

    @Test
    void getTransactionsByDate_ShouldReturnListOfTransactions_WhenTransactionsExist() {

        when(transactionsRepository.findTransactionsByTransactionDate(any())).thenReturn(List.of(transaction));

        List<Transactions> transactionsFound = transactionsService.getTransactionsByDate(any());

        assertNotNull(transactionsFound);
        assertEquals(1, transactionsFound.size());
        assertEquals("buy", transactionsFound.get(0).getOperation());
    }

    @Test
    void getTransactionsByDate_ShouldThrowTransactionNotFoundException_WhenTransactionsExist() {

        when(transactionsRepository.findTransactionsByTransactionDate(any())).thenReturn(List.of());


        List<Transactions> transactionsFound = transactionsService.getTransactionsByDate(any());

        assertNotNull(transactionsFound);
        assertEquals(1, transactionsFound.size());
        assertEquals("buy", transactionsFound.get(0).getOperation());
    }


    @Test
    void getAllTransactionsByCustomerId() {
    }

    @Test
    void saveTransaction() {
    }
}