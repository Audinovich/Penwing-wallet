package com.Testing.practicasTesteo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long WalletId;

    private String name;

    private String address;


    @ManyToMany(
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "Wallet_customer_map",
            joinColumns = @JoinColumn(
                    name = "Wallet_id",
                    referencedColumnName = "WalletId"
            ),
            inverseJoinColumns = @JoinColumn(
                    name="customer_id",
                    referencedColumnName ="customerId"
            )
    )
    private List<Customer> customerList;


@ManyToMany(
        cascade = CascadeType.ALL
)
@JoinTable(
        name = "Wallet_article_map",
        joinColumns = @JoinColumn(
                name = "Wallet_id",
                referencedColumnName = "WalletId"
        ),
        inverseJoinColumns = @JoinColumn(
                name="article_id",
                referencedColumnName ="articleId"
        )
)
private List<Article> articleList;

}