package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CreditRepository extends JpaRepository <Credit, Long> {

    @Query("SELECT cr FROM Credit cr JOIN cr.customer cust WHERE cust.customerId = :customerId")
    List<Credit> getAllCreditsByCustomerId(Long customerId);

    @Query("SELECT cr FROM Credit cr WHERE cr.customer.customerId = :customerId")
    Optional<Credit> findMoneyByCustomerId(@Param("customerId") Long customerId);


}
