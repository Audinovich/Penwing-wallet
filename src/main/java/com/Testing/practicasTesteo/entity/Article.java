package com.Testing.practicasTesteo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long articleId;

    private String symbol;
    private String name;
    private String image;
    private double currentPrice; // double
    private BigInteger marketCap; // BigInteger
    private BigInteger totalVolume; // BigInteger
    private double high24h; // double
    private double low24h; // double
    private double priceChange24h; // double
    private double priceChangePercentage24h; // double
    private BigInteger marketCapChange24h; // BigInteger
    private double marketCapChangePercentage24h; // double
    private BigInteger circulatingSupply; // BigInteger
    private BigInteger totalSupply; // BigInteger
    private double ath; // double
    private double athChangePercentage; // double
    private String athDate;
    private String lastUpdated;

    @ManyToMany(mappedBy = "articles")
    @JsonIgnore
    private List<Wallet> wallets = new ArrayList<>();

    public Article(String symbol, String name, String image, double currentPrice, BigInteger marketCap, BigInteger totalVolume, double high24h, double low24h, double priceChange24h, double priceChangePercentage24h, BigInteger marketCapChange24h, double marketCapChangePercentage24h, BigInteger circulatingSupply, BigInteger totalSupply, double ath, double athChangePercentage, String athDate, String lastUpdated) {
        this.symbol = symbol;
        this.name = name;
        this.image = image;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.totalVolume = totalVolume;
        this.high24h = high24h;
        this.low24h = low24h;
        this.priceChange24h = priceChange24h;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.marketCapChange24h = marketCapChange24h;
        this.marketCapChangePercentage24h = marketCapChangePercentage24h;
        this.circulatingSupply = circulatingSupply;
        this.totalSupply = totalSupply;
        this.ath = ath;
        this.athChangePercentage = athChangePercentage;
        this.athDate = athDate;
        this.lastUpdated = lastUpdated;
    }

}
