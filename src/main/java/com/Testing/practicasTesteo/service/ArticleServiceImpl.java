package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleRepository articleRepository;


    @Override
    public List<Article> getAllArticles() throws ArticleNotFoundException, ArticleFetchException {
        try {
            List<Article> articleList = articleRepository.findAll();
            if (articleList.isEmpty()) {
                throw new ArticleNotFoundException("No data found");
            }
            return articleList;
        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing article data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public Article getArticleById(long id) throws ArticleNotFoundException {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found with id: " + id));
    }

    @Override
    public Article updateArticleById(Article article, long id) {
        Optional<Article> articleFound = articleRepository.findById(id);
        if (articleFound.isPresent()) {
            Article articleUpdated = articleFound.get();
            articleUpdated.setSymbol(article.getSymbol());
            articleUpdated.setName(article.getName());
            articleUpdated.setImage(article.getImage());
            articleUpdated.setCurrentPrice(article.getCurrentPrice());
            articleUpdated.setMarketCap(article.getMarketCap());
            articleUpdated.setTotalVolume(article.getTotalVolume());
            articleUpdated.setHigh24h(article.getHigh24h());
            articleUpdated.setLow24h(article.getLow24h());
            articleUpdated.setPriceChange24h(article.getPriceChange24h());
            articleUpdated.setPriceChangePercentage24h(article.getPriceChangePercentage24h());
            articleUpdated.setMarketCapChange24h(article.getMarketCapChange24h());
            articleUpdated.setMarketCapChangePercentage24h(article.getMarketCapChangePercentage24h());
            articleUpdated.setCirculatingSupply(article.getCirculatingSupply());
            articleUpdated.setTotalSupply(article.getTotalSupply());
            articleUpdated.setAth(article.getAth());
            articleUpdated.setAthChangePercentage(article.getAthChangePercentage());
            articleUpdated.setAthDate(article.getAthDate());
            articleUpdated.setLastUpdated(article.getLastUpdated());

            return articleRepository.save(articleUpdated);
        } else {
            throw new ArticleNotFoundException("Article ID " + id + " no encontrado.");
        }
    }


    @Override
    public Article saveArticle(Article article) {
        try {
            return articleRepository.save(article);
        } catch (Exception e) {
            throw new NotSavedException("Error saving article" + e.getMessage(), e);
        }
    }
/*
    @Override
    public List<Article> saveArticles(List<Article> articles) {
        try {
            // Registrar los artículos que se están intentando guardar
            System.out.println("Intentando guardar los siguientes artículos: " + articles);

            List<Article> savedArticles = articleRepository.saveAll(articles);

            // Verificar si los artículos fueron guardados
            System.out.println("Artículos guardados con éxito: " + savedArticles);

            return savedArticles;
        } catch (Exception e) {
            // Registrar la excepción con más detalles
            System.err.println("Error al guardar los artículos: " + e.getMessage());
            throw new NotSavedException("Error saving articles: " + e.getMessage(), e);
        }
    }
*/
    @Override
    public boolean deleteAllArticles() {

        try {
            articleRepository.deleteAll();
            return true;

        } catch (Exception e) {
            throw new NotDeletedException("Articles not Deleted." + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteArticleById(long id) {
        Optional<Article> articleFound = articleRepository.findById(id);
        if (articleFound.isPresent()) {
            articleRepository.delete(articleFound.get());
            return true;
        } else {
            throw new ArticleNotFoundException("Article not found with id: " + id);
        }
    }

    @Override
    public List<Article> fetchCryptoData(boolean mock) throws IOException {
        if (mock) {
            return getMockCryptos();
        } else {
            return fetchCryptoDataFromAPI();
        }
    }

    @Override
    public List<Article> getArticlesByCustomerId(Long customerId) throws ArticleNotFoundException, ArticleFetchException {
        try {
            List<Article> articlesFound = articleRepository.getAllArticlesByCustomerId(customerId);
            if (articlesFound.isEmpty()) {
                throw new ArticleNotFoundException("No data found");
            }
            return articlesFound;
        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing article data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Article> getMockCryptos() throws IOException {
        List<Article> cryptos = new ArrayList<>();
        cryptos.add(new Article("bitcoin", "btc", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", 45000, new BigInteger("850000000000"), new BigInteger("50000000000"), 46000, 44000, -1000, -2.17, new BigInteger("10000000000"), -1.17, new BigInteger("18000000"), new BigInteger("21000000"), 69000, -34.83, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        cryptos.add(new Article("ethereum", "eth", "https://assets.coingecko.com/coins/images/279/large/ethereum.png", 3000, new BigInteger("350000000000"), new BigInteger("20000000000"), 3200, 2900, -100, -3.23, new BigInteger("5000000000"), -1.42, new BigInteger("115000000"), new BigInteger("120000000"), 4800, -37.5, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        cryptos.add(new Article("ripple", "xrp", "https://assets.coingecko.com/coins/images/44/large/ripple.png", 0.80, new BigInteger("40000000000"), new BigInteger("1500000000"), 0.85, 0.75, -0.02, -2.44, new BigInteger("500000000"), -1.25, new BigInteger("50000000000"), new BigInteger("100000000000"), 3.84, -79.17, "2018-01-07T00:00:00Z", "2024-10-12T20:36:50Z"));
        cryptos.add(new Article("litecoin", "ltc", "https://assets.coingecko.com/coins/images/2/large/litecoin.png", 150, new BigInteger("10000000000"), new BigInteger("500000000"), 160, 140, -5, -3.23, new BigInteger("300000000"), -2.92, new BigInteger("84000000"), new BigInteger("84000000"), 400, -62.5, "2021-05-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        cryptos.add(new Article("cardano", "ada", "https://assets.coingecko.com/coins/images/975/large/cardano.png", 0.50, new BigInteger("16000000000"), new BigInteger("700000000"), 0.55, 0.48, -0.02, -3.85, new BigInteger("200000000"), -1.23, new BigInteger("32000000000"), new BigInteger("450000000"), 3.10, -83.87, "2021-09-02T00:00:00Z", "2024-10-12T20:36:50Z"));
        return cryptos;
    }
    @Override
    public List<Article> saveArticles(List<Article> articles) {
        try {
            return articleRepository.saveAll(articles);
        } catch (Exception e) {
            throw new NotSavedException("Error saving articles: " + e.getMessage(), e);
        }
    }


    private List<Article> fetchCryptoDataFromAPI() throws IOException {
        String apiUrl = "https://api.ejemplo.com/cryptodata";
        String apiKey = "API_KEY";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), new TypeReference<List<Article>>() {
            });
        } else {
            throw new IOException("Error al llamar a la API: " + response.statusCode());
        }
    }
}
