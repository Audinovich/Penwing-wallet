package com.Testing.practicasTesteo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Credit")
@JsonIgnoreProperties({"customer"})
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditId;

    private BigDecimal euro;
    private BigDecimal bitcoin;
    private BigDecimal ethereum;
    private BigDecimal ripple;
    private BigDecimal litecoin;
    private BigDecimal cardano;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    @JsonManagedReference
    private Customer customer;


}