package com.Testing.practicasTesteo.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long transactionId;

    LocalDateTime transactionDate;

    String description;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    @JsonManagedReference
    private Customer customer;

}
