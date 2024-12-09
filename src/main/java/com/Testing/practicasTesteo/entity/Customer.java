package com.Testing.practicasTesteo.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long customerId;

    String name;
    String surname;
    String password;
    String address;
    String email;
    String phone;
    //String code;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Wallet wallet;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Credit> credits = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Transactions> transactions = new ArrayList<>();

}
