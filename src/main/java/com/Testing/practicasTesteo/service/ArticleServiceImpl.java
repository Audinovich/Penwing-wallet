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
import org.springframework.beans.factory.annotation.Value;
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
    private ArticleRepository articleRepository;

    @Value("${mockdata}")
    Boolean mockData;

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
        }
    }

    @Override
    public Article getArticleById(long id) throws ArticleNotFoundException {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found with id: " + id));
    }

    @Override
    public Article updateArticleById(Article article, long id) throws ArticleNotFoundException {
        Article articleToUpdate = getArticleById(id); // Usar el método getArticleById
        updateArticleFields(articleToUpdate, article);
        return articleRepository.save(articleToUpdate);
    }

    private void updateArticleFields(Article articleToUpdate, Article newArticleData) {
        articleToUpdate.setSymbol(newArticleData.getSymbol());
        articleToUpdate.setName(newArticleData.getName());
        articleToUpdate.setImage(newArticleData.getImage());
        articleToUpdate.setCurrentPrice(newArticleData.getCurrentPrice());
        articleToUpdate.setMarketCap(newArticleData.getMarketCap());
        articleToUpdate.setTotalVolume(newArticleData.getTotalVolume());
        articleToUpdate.setHigh24h(newArticleData.getHigh24h());
        articleToUpdate.setLow24h(newArticleData.getLow24h());
        articleToUpdate.setPriceChange24h(newArticleData.getPriceChange24h());
        articleToUpdate.setPriceChangePercentage24h(newArticleData.getPriceChangePercentage24h());
        articleToUpdate.setMarketCapChange24h(newArticleData.getMarketCapChange24h());
        articleToUpdate.setMarketCapChangePercentage24h(newArticleData.getMarketCapChangePercentage24h());
        articleToUpdate.setCirculatingSupply(newArticleData.getCirculatingSupply());
        articleToUpdate.setTotalSupply(newArticleData.getTotalSupply());
        articleToUpdate.setAth(newArticleData.getAth());
        articleToUpdate.setAthChangePercentage(newArticleData.getAthChangePercentage());
        articleToUpdate.setAthDate(newArticleData.getAthDate());
        articleToUpdate.setLastUpdated(newArticleData.getLastUpdated());
    }

    @Override
    public Article saveArticle(Article article) throws NotSavedException {
        try {
            return articleRepository.save(article);
        } catch (Exception e) {
            throw new NotSavedException("Error saving article: " + e.getMessage(), e);
        }
    }

    public List<Article> saveArticles(List<Article> articles) throws NotSavedException {
        List<Article> savedArticles = new ArrayList<>();
        for (Article article : articles) {
            try {
                savedArticles.add(saveArticle(article));
            } catch (NotSavedException e) {
                System.err.println("Failed to save article: " + article.getName() + " - " + e.getMessage());
            }
        }
        return savedArticles;
    }

    @Override
    public boolean deleteAllArticles() {
        try {
            articleRepository.deleteAll();
            return true;
        } catch (Exception e) {
            throw new NotDeletedException("Articles not deleted: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteArticleById(long id) throws ArticleNotFoundException {
        Article articleToDelete = getArticleById(id);
        articleRepository.delete(articleToDelete);
        return true;
    }
    // ternario para ver si consumo MOCK o API
    @Override
    public List<Article> fetchCryptoData() throws IOException {
        return mockData ? getMockCryptos() : fetchCryptoDataFromAPI();
    }

    @Override
    public List<Article> getArticlesByCustomerId(Long customerId) throws ArticleNotFoundException, ArticleFetchException {
        try {
            List<Article> articlesFound = articleRepository.getAllArticlesByCustomerId(customerId);
            if (articlesFound.isEmpty()) {
                throw new ArticleNotFoundException("No data found for customer ID: " + customerId);
            }
            return articlesFound;
        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing article data: " + e.getMessage(), e);
        }
    }



    @Override
    public List<Article> getMockCryptos() {
        List<Article> existingArticles = articleRepository.findAll(); // Obtener artículos existentes directamente de la BDD
        List<Article> newCryptos = new ArrayList<>(); // Lista de artículos nuevos a añadir

        // Artículos mock actualizados
        newCryptos.add(new Article("bitcoin", "btc", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", 63562, new BigInteger("850000000000"), new BigInteger("50000000000"), 46000, 44000, -1000, -2.17, new BigInteger("10000000000"), -1.17, new BigInteger("18000000"), new BigInteger("21000000"), 69000, -34.83, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("ethereum", "eth", "https://assets.coingecko.com/coins/images/279/large/ethereum.png", 3000, new BigInteger("350000000000"), new BigInteger("20000000000"), 3200, 2900, -100, -3.23, new BigInteger("5000000000"), -1.42, new BigInteger("115000000"), new BigInteger("120000000"), 4800, -37.5, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("ripple", "xrp", "https://assets.coingecko.com/coins/images/44/large/ripple.png", 0.80, new BigInteger("40000000000"), new BigInteger("1500000000"), 0.85, 0.75, -0.02, -2.44, new BigInteger("500000000"), -1.25, new BigInteger("50000000000"), new BigInteger("100000000000"), 3.84, -79.17, "2018-01-07T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("litecoin", "ltc", "https://assets.coingecko.com/coins/images/2/large/litecoin.png", 150, new BigInteger("10000000000"), new BigInteger("500000000"), 160, 140, -5, -3.23, new BigInteger("300000000"), -2.92, new BigInteger("84000000"), new BigInteger("84000000"), 400, -62.5, "2021-05-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("cardano", "ada", "https://assets.coingecko.com/coins/images/975/large/cardano.png", 0.50, new BigInteger("16000000000"), new BigInteger("700000000"), 0.55, 0.48, -0.02, -3.85, new BigInteger("200000000"), -1.23, new BigInteger("32000000000"), new BigInteger("450000000"), 3.10, -83.87, "2021-09-02T00:00:00Z", "2024-10-12T20:36:50Z"));

        // Actualizar los valores de los artículos existentes o añadir nuevos
        for (Article newCrypto : newCryptos) {
            Optional<Article> existingCryptoOpt = existingArticles.stream()
                    .filter(article -> article.getSymbol().equals(newCrypto.getSymbol()))
                    .findFirst();

            if (existingCryptoOpt.isPresent()) {
                // Si el artículo ya existe, actualizar sus valores
                updateArticleFields(existingCryptoOpt.get(), newCrypto);
                // Guardar el artículo actualizado en la base de datos
                saveArticle(existingCryptoOpt.get());
            } else {
                // Si no existe, agregarlo a la lista de artículos existentes
                existingArticles.add(newCrypto);
                // Guardar el nuevo artículo en la base de datos
                saveArticle(newCrypto);
            }
        }

        return existingArticles; // Devuelve la lista de artículos actualizados
    }


    private List<Article> fetchCryptoDataFromAPI() throws IOException {
        String apiUrl = "https://api.ejemplo.com/cryptodata"; // Cambia esto a la URL correcta
        String apiKey = "API_KEY"; // Reemplaza esto con tu clave API
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
            throw new RuntimeException("Error while fetching crypto data: " + e.getMessage(), e);
        }
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), new TypeReference<List<Article>>() {});
        } else {
            throw new IOException("Error fetching data from API: " + response.statusCode());
        }
    }
}