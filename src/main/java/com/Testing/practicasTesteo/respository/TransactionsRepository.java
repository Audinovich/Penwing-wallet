package com.Testing.practicasTesteo.respository;


import com.Testing.practicasTesteo.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionsRepository extends JpaRepository <Transactions,Long> {


    List<Transactions> findTransactionsByTransactionDate (LocalDateTime transactionDate);
}
