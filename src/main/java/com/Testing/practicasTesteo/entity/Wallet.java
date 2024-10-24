package com.Testing.practicasTesteo.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private String name;
    long cuantity;



    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    @JsonBackReference
    private Customer customer;

   @ManyToMany

   @JoinTable(
           name = "Wallet_Article",
           joinColumns = @JoinColumn(name = "wallet_id"),
           inverseJoinColumns = @JoinColumn(name = "article_id")
   )
   private List<Article> articles = new ArrayList<>();
}