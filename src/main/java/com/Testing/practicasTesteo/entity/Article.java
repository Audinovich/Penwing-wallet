package com.Testing.practicasTesteo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long articleId;

    String codigo;

    @Column(name = "DESCRIPCION_DE_ARTICULO")
    String descripcion;

    int precio;


}
