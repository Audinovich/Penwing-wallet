package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

    //conexion con el repositorio a traves de constructor
    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Value("${mockdata}")
    Boolean mockData;

    @Value("$spring.jpa.hibernate.ddl-auto")
    String bbddValue;


    private static final String CREATE = "create";

    @PostConstruct
    public void loadMockDataIfNeeded() {
        if (bbddValue.toLowerCase().equals(CREATE) && mockData) {
            getMockCryptos();
        }
    }

    @Override
    public List<Article> getAllArticles() throws ArticleNotFoundException, ArticleFetchException {
        try {
            List<Article> articleList = articleRepository.findAll();
            if (articleList.isEmpty()) {
                throw new ArticleNotFoundException("Articles not found");
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

    //PROPAGA LA EXCEPCION QUE RECIBE DE GETARTICLEBYID Y LA MANDA AL CONTROLADOR, SI NO TUVIERA EL THROWS DEBERIA USAR TRY CATCH
    @Override
    public Article updateArticleById(Article article, long id) throws ArticleNotFoundException {
        Article articleToUpdate = getArticleById(id);
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
        } catch (RuntimeException e) {
            throw new NotSavedException("Failed to save article due to a persistence issue: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NotSavedException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    public List<Article> saveArticles(List<Article> articles) throws NotSavedException {
        try {
            return articleRepository.saveAll(articles);
        } catch (RuntimeException e) {
            throw new NotSavedException("Failed to save articles due to a persistence issue: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NotSavedException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteAllArticles() throws NotDeletedException {
        try {
            articleRepository.deleteAll();
            return true;
        } catch (RuntimeException e) {
            throw new NotDeletedException("Articles not deleted: " + e.getMessage(), e);
        }catch (Exception e){
            throw new NotDeletedException("An unexpected error occurred: "  + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteArticleById(long articleId) throws ArticleNotFoundException, NotDeletedException {

        try {
            articleRepository.deleteById(articleId);
            return true;
        } catch (RuntimeException e) {
            throw new NotDeletedException("Article Not deleted: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NotDeletedException("An unexpected error occurred: "  + e.getMessage(), e);
        }
    }

    @Override
    public List<Article> getArticlesByCustomerId(Long customerId) throws ArticleNotFoundException, ArticleFetchException {
        try {
            List<Article> articlesFound = articleRepository.getAllArticlesByCustomerId(customerId);
            if (articlesFound.isEmpty()) {
                throw new ArticleNotFoundException("No data found for customer ID: " + customerId);
            }
            return articlesFound;
        } catch (ArticleNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ArticleFetchException("Error accessing article data: " + e.getMessage(), e);
        }
    }


    // ternario para ver si consumo MOCK o API
    @Override
    public List<Article> fetchCryptoData() throws IOException {
        System.out.println("Valor de mockData: " + mockData);
        return mockData ? getMockCryptos() : fetchCryptoDataFromAPI();
    }

    //BUSCA LOS ARTICULOS EN LA BD POR SYMBOL Y SI NO ENCUENTRA LOS CREA, O ACTUALIZA
    @Override
    public List<Article> getMockCryptos() {
        List<Article> existingArticles = articleRepository.findAll();
        List<Article> newCryptos = new ArrayList<>();


        newCryptos.add(new Article("bitcoin", "btc", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", 63562, new BigInteger("850000000000"), new BigInteger("50000000000"), 46000, 44000, -1000, -2.17, new BigInteger("10000000000"), -1.17, new BigInteger("18000000"), new BigInteger("21000000"), 69000, -34.83, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("ethereum", "eth", "https://assets.coingecko.com/coins/images/279/large/ethereum.png", 3000, new BigInteger("350000000000"), new BigInteger("20000000000"), 3200, 2900, -100, -3.23, new BigInteger("5000000000"), -1.42, new BigInteger("115000000"), new BigInteger("120000000"), 4800, -37.5, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("ripple", "xrp", "https://assets.coingecko.com/coins/images/44/large/ripple.png", 0.80, new BigInteger("40000000000"), new BigInteger("1500000000"), 0.85, 0.75, -0.02, -2.44, new BigInteger("500000000"), -1.25, new BigInteger("50000000000"), new BigInteger("100000000000"), 3.84, -79.17, "2018-01-07T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("litecoin", "ltc", "https://assets.coingecko.com/coins/images/2/large/litecoin.png", 150, new BigInteger("10000000000"), new BigInteger("500000000"), 160, 140, -5, -3.23, new BigInteger("300000000"), -2.92, new BigInteger("84000000"), new BigInteger("84000000"), 400, -62.5, "2021-05-10T00:00:00Z", "2024-10-12T20:36:50Z"));
        newCryptos.add(new Article("cardano", "ada", "https://assets.coingecko.com/coins/images/975/large/cardano.png", 0.50, new BigInteger("16000000000"), new BigInteger("700000000"), 0.55, 0.48, -0.02, -3.85, new BigInteger("200000000"), -1.23, new BigInteger("32000000000"), new BigInteger("450000000"), 3.10, -83.87, "2021-09-02T00:00:00Z", "2024-10-12T20:36:50Z"));


        for (Article newCrypto : newCryptos) {
            Optional<Article> existingCryptoOpt = existingArticles.stream()
                    .filter(article -> article.getSymbol().equals(newCrypto.getSymbol()))
                    .findFirst();

            if (existingCryptoOpt.isPresent()) {

                updateArticleFields(existingCryptoOpt.get(), newCrypto);

                saveArticle(existingCryptoOpt.get());
            } else {

                existingArticles.add(newCrypto);

                saveArticle(newCrypto);
            }
        }

        return existingArticles;
    }
    //SOLICITUD API EXTERNA HTTP , MANEJO EXCEPCIONES Y DESERIALIZA OBJECTMAPPER
    //TODO ver el tema de la APIKEY
    List<Article> fetchCryptoDataFromAPI() throws IOException {
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
            throw new RuntimeException("Error while fetching crypto data: " + e.getMessage(), e);
        }
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), new TypeReference<List<Article>>() {
            });
        } else {
            throw new IOException("Error fetching data from API: " + response.statusCode());
        }
    }
}