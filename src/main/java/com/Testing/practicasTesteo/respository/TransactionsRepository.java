package com.Testing.practicasTesteo.respository;


import com.Testing.practicasTesteo.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionsRepository extends JpaRepository <Transactions,Long> {

    @Query("SELECT t FROM Transactions t WHERE t.transactionDate = :transactionDate")
    List<Transactions> findTransactionsByTransactionDate (LocalDateTime transactionDate);

    @Query("SELECT t FROM Transactions t WHERE t.customer.customerId = :customerId")
    List<Transactions> findTransactionsByCustomerId (Long customerId);
}
