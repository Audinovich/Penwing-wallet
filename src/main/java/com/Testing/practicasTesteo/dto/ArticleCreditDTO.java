package com.Testing.practicasTesteo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreditDTO {

        private String symbol;
        private String name;
        private String image;
        private Double creditAmount;
        private Double currentPrice;
        private Double euroBalance;


}


