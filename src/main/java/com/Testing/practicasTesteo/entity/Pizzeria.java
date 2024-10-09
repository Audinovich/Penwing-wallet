package com.Testing.practicasTesteo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Pizzeria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pizzeria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pizzeriaId;

    private String name;

    private String address;


    @ManyToMany(
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "pizzeria_customer_map",
            joinColumns = @JoinColumn(
                    name = "local_id",
                    referencedColumnName = "pizzeriaId"
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
        name = "pizzeria_article_map",
        joinColumns = @JoinColumn(
                name = "local_id",
                referencedColumnName = "pizzeriaId"
        ),
        inverseJoinColumns = @JoinColumn(
                name="article_id",
                referencedColumnName ="articleId"
        )
)
private List<Article> articleList;

}