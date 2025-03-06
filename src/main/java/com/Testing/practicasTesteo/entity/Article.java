package com.Testing.practicasTesteo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image")
    private String image;
    @JsonProperty("current_price")
    private double currentPrice; // double
    @JsonProperty("market_cap")
    private BigInteger marketCap; // BigInteger
    @JsonProperty("total_volume")
    private BigInteger totalVolume; // BigInteger
    @JsonProperty("high_24h")
    private double high24h; // double
    @JsonProperty("low_24h")
    private double low24h; // double
    @JsonProperty("price_change_24h")
    private double priceChange24h; // double
    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h; // double
    @JsonProperty("market_cap_change_24h")
    private BigInteger marketCapChange24h; // BigInteger
    @JsonProperty("market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h; // double
    @JsonProperty("circulating_supply")
    private BigInteger circulatingSupply; // BigInteger
    @JsonProperty("total_supply")
    private BigInteger totalSupply; // BigInteger
    @JsonProperty("ath")
    private double ath; // double
    @JsonProperty("ath_change_percentage")
    private double athChangePercentage; // double
    @JsonProperty("ath_date")
    private String athDate;
    @JsonProperty("last_updated")
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
