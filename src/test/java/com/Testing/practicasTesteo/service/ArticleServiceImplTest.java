package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@SpringBootTest
class ArticleServiceImplTest {


    @MockBean
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    private Article mockArticle;
    private Article mockArticle2;

    @BeforeEach
    void setUp() {
        mockArticle = Article.builder()
                .articleId(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .currentPrice(50000.0)
                .marketCap(BigInteger.valueOf(1000000000))

                .build();
        mockArticle2 = Article.builder()
                .articleId(2L)
                .name("Ethereum")
                .symbol("ETH")
                .currentPrice(4000.0)
                .marketCap(BigInteger.valueOf(500000000))
                .build();


    }

    @Test
    void loadMockDataIfNeeded() {
    }

    @Test
    void getAllArticles_ShouldReturnOK() throws Exception {

        List<Article> articles = List.of(mockArticle);

        when(articleRepository.findAll()).thenReturn(articles);

        List<Article> result = articleService.getAllArticles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bitcoin", result.get(0).getName());
        verify(articleRepository, times(1)).findAll();


    }

    @Test
    void getArticleById_ShouldReturnArticle() throws ArticleNotFoundException {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(mockArticle));

        Article result = articleService.getArticleById(1L);

        assertNotNull(result);
        assertEquals("Bitcoin", result.getName());

        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void getArticleById_ShouldThrowArticleNotFoundException_WhenArticleNotFound() {

        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        ArticleNotFoundException articleException = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.getArticleById(1L);
        });

        assertEquals("Article not found with id: " + 1L, articleException.getMessage());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void getArticleById_ShouldThrowInternalServerError() {

        when(articleRepository.findById(1L)).thenThrow(new RuntimeException("Internal server error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.getArticleById(1L);
        });
        assertEquals("Internal server error", exception.getMessage());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void updateArticleById() {
    }

    @Test
    void saveArticleShouldReturnSavedArticle() {
        when(articleRepository.save(mockArticle)).thenReturn(mockArticle);

        Article savedArticle = articleService.saveArticle(mockArticle);

        assertNotNull(savedArticle, "El artículo guardado no debe ser nulo");
        assertEquals(mockArticle.getArticleId(), savedArticle.getArticleId(), "Los IDs del artículo no coinciden");
        assertEquals(mockArticle.getName(), savedArticle.getName(), "Los nombres del artículo no coinciden");
        assertEquals(mockArticle.getSymbol(), savedArticle.getSymbol(), "Los símbolos del artículo no coinciden");
        assertEquals(mockArticle.getCurrentPrice(), savedArticle.getCurrentPrice(), "Los precios del artículo no coinciden");
        assertEquals(mockArticle.getMarketCap(), savedArticle.getMarketCap(), "Las capitalizaciones de mercado del artículo no coinciden");

        // 5. Verificamos que el repositorio fue llamado exactamente una vez para guardar el artículo
        verify(articleRepository, times(1)).save(mockArticle);
    }

    @Test
    void saveArticleShouldReturnNotSavedExceptionByRuntimeException() {
        when(articleRepository.save(mockArticle)).thenThrow(new RuntimeException("Internal Server Error"));

        NotSavedException notSavedException = assertThrows(NotSavedException.class, () -> {
            articleService.saveArticle(mockArticle);
        });
        assertEquals("Failed to save article due to a persistence issue: Internal Server Error", notSavedException.getMessage());
        verify(articleRepository, times(1)).save(mockArticle);
    }


    @Test
    void saveArticleShouldThrowNotSavedException_ForCheckedException() {

        when(articleRepository.save(mockArticle)).thenAnswer(invocation -> {
            throw new Exception("Checked exception triggered");
        });
        NotSavedException notSavedException = assertThrows(NotSavedException.class, () -> {
            articleService.saveArticle(mockArticle);
        });
        assertEquals("An unexpected error occurred: Checked exception triggered", notSavedException.getMessage());
        verify(articleRepository, times(1)).save(mockArticle);
    }

    @Test
    void saveArticlesShouldReturn_SavedArticles() {

        List<Article> articlesToSave = List.of(mockArticle, mockArticle2);
        when(articleRepository.saveAll(articlesToSave)).thenReturn(articlesToSave);


        List<Article> savedArticles = articleService.saveArticles(articlesToSave);

        assertNotNull(savedArticles, "La lista de artículos guardados no debe ser nula");
        assertEquals(2, savedArticles.size(), "La lista debe contener exactamente 2 artículos");
        assertEquals("Bitcoin", savedArticles.get(0).getName(), "El primer artículo debe ser 'Bitcoin'");
        assertEquals("Ethereum", savedArticles.get(1).getName(), "El segundo artículo debe ser 'Ethereum'");


        verify(articleRepository, times(1)).saveAll(articlesToSave);

    }

    @Test
    void saveArticlesShouldThrow_NotSaved() {
        List<Article> mockArticleList = List.of(mockArticle, mockArticle2);

        when(articleRepository.saveAll(mockArticleList))
                .thenThrow(new RuntimeException("Internal Server Error"));

        NotSavedException notSavedException = assertThrows(NotSavedException.class, () -> {
            articleService.saveArticles(mockArticleList);
        });
        assertEquals("Failed to save articles due to a persistence issue: Internal Server Error", notSavedException.getMessage());
        verify(articleRepository, times(1)).saveAll(mockArticleList);
    }


    //todo Revisar este test- then throw solo funciona para excepciones no comprobadas unchecked
    //todo conviene declarar en la firma del metodo en el servicio para usar thenthrow? NO
    @Test
    void saveArticlesShouldThrow_NotSavedException_WhenGenericExceptionOccurs() {
        // Arrange
        List<Article> mockArticleList = List.of(mockArticle, mockArticle2);

        //METODO NO VOID CON EXCEPCION CHECKED - WHEN. THENANSWER
        when(articleRepository.saveAll(mockArticleList)).thenAnswer(invocation -> {
            throw new Exception("Unexpected error");

        });
        NotSavedException exception = assertThrows(NotSavedException.class,
                () -> articleService.saveArticles(mockArticleList)
        );

        assertEquals("An unexpected error occurred: Unexpected error", exception.getMessage());
        verify(articleRepository, times(1)).saveAll(mockArticleList);
    }


    //TODO REVISAR ESTE TEST - ES NECESARIO AGREGAR LOS THROWS EN LOS TEST?
    @Test
    void deleteArticleByIdShouldDeleteArticleSuccessfully() throws ArticleNotFoundException, NotDeletedException {

        articleService.deleteArticleById(mockArticle.getArticleId());

        verify(articleRepository, times(1)).deleteById(mockArticle.getArticleId());
    }

    //TODO REVISAR ESTE TEST se usa when por que el metodo deletebyId es void por repositorio asi que se usa dothrow
    @Test
    void deleteArticleByIdShouldThrowNotDeletedException_WhenRuntimeExceptionOccurs() {

        //METODO VOID CON EXCEPCION CHECKED - DO THROW.WHEN
        doThrow(new RuntimeException("Error"))
                .when(articleRepository).deleteById(1L);

        //INVOCA METODO deleteArticleById(1L) EL REPO DEVUELVE EXCEPCION SERVICIO TRADUCE A NOTDELETEDEXCEPTION
        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            articleService.deleteArticleById(1L);
        });

        assertEquals("Article Not deleted: Error", exception.getMessage());


        verify(articleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteArticleByIdShouldReturnNotDeletedException_WhenExceptionOccurs() {

        //METODO VOID CON EXCEPCION CHECKED DO ANSWER.WHEN
        doAnswer(invocation -> {
            throw new Exception("Error");
        }).when(articleRepository).deleteById(1L);

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            articleService.deleteArticleById(1L);
        });
        assertEquals("An unexpected error occurred: Error", exception.getMessage());
        verify(articleRepository, times(1)).deleteById(1L);

    }

    @Test
    void deleteAllArticlesShouldDeleteArticlesSuccessfully() {

        List<Article> articlesList = List.of(mockArticle, mockArticle2);
        articleRepository.deleteAll(articlesList);

        verify(articleRepository, times(1)).deleteAll(articlesList);

    }

    @Test
    void deleteAllArticlesShouldThrowNotDeletedException_WhenRuntimeExceptionOccurs() {

        doThrow(new RuntimeException("Error deleting Articles."))
                .when(articleRepository).deleteAll();

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            articleService.deleteAllArticles();

        });
        assertEquals("Articles not deleted: Error deleting Articles.", exception.getMessage());

        verify(articleRepository, times(1)).deleteAll();
    }

    @Test
    void deleteAllArticlesShouldReturnNotDeletedException_WhenExceptionOccurs() {

        // TODO REVISAR ANOTACION INVOCATION O INVOCATION ON MOCK.

        doAnswer(invocation -> {
          throw new Exception("Internal Error");}).when(articleRepository).deleteAll();

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            articleService.deleteAllArticles();
        });

        assertEquals("An unexpected error occurred: Internal Error", exception.getMessage());

        verify(articleRepository, times(1)).deleteAll();

    }

    @Test
    void getArticlesByCustomerIdShouldGetArticlesSuccessfully() throws ArticleFetchException {

        List<Article> articlesList = List.of(mockArticle, mockArticle2);
        when(articleRepository.getAllArticlesByCustomerId(1L)).thenReturn(articlesList);

        List<Article> result = articleService.getArticlesByCustomerId(1L);

        assertNotNull(articlesList);
        assertEquals("Bitcoin", articlesList.get(0).getName());
        assertEquals("Ethereum", articlesList.get(1).getName());

        verify(articleRepository, times(1)).getAllArticlesByCustomerId(1L);

    }

    @Test
    void getArticlesByCustomerIdShouldThrowArticleNotFoundExceptionWhenNoArticlesFound() {

        when(articleRepository.getAllArticlesByCustomerId(1L)).thenReturn(Collections.emptyList());

        ArticleNotFoundException exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.getArticlesByCustomerId(1L);
        });

        assertEquals("No data found for customer ID: " + 1L, exception.getMessage());
        verify(articleRepository, times(1)).getAllArticlesByCustomerId(1L);
    }

    @Test
    void getArticlesByCustomerIdShouldThrowArticleFetchException_WhenDataAccessExceptionOccurs() {

        doAnswer(invocationOnMock -> {
            throw new Exception("Internal Error");
        })
                .when(articleRepository).getAllArticlesByCustomerId(1L);

        ArticleFetchException exception = assertThrows(ArticleFetchException.class, () -> {
            articleService.getArticlesByCustomerId(1L);
        });
        assertEquals("Error accessing article data: Internal Error", exception.getMessage());
        verify(articleRepository, times(1)).getAllArticlesByCustomerId(1L);
    }


    //TODO REVISAR CON ANDRES
    @Test
    void fetchCryptoData_mockDataTrue_returnsMockCryptos() throws IOException {

        ArticleServiceImpl articleService = new ArticleServiceImpl(null);
        articleService.mockData = true;

        List<Article> mockCryptos = List.of(mockArticle, mockArticle2);
        ArticleServiceImpl spyService = Mockito.spy(articleService);
        doReturn(mockCryptos).when(spyService).getMockCryptos();

        List<Article> result = spyService.fetchCryptoData();

        // Verificar que se llama a getMockCryptos y no a fetchCryptoDataFromAPI
        verify(spyService, times(1)).getMockCryptos();
        verify(spyService, never()).fetchCryptoDataFromAPI();
        assertEquals(mockCryptos, result);
    }

    @Test
    void fetchCryptoData_mockDataFalse_callsFetchCryptoDataFromAPI() throws IOException {
        // Crear instancia real del servicio y simular métodos
        ArticleServiceImpl articleService = new ArticleServiceImpl(null);
        articleService.mockData = false;

        // Simular el comportamiento de fetchCryptoDataFromAPI
        List<Article> apiData = List.of(mockArticle, mockArticle2
        );
        ArticleServiceImpl spyService = Mockito.spy(articleService);
        doReturn(apiData).when(spyService).fetchCryptoDataFromAPI();

        // Ejecutar el método
        List<Article> result = spyService.fetchCryptoData();

        // Verificar que se llama a fetchCryptoDataFromAPI y no a getMockCryptos
        verify(spyService, times(1)).fetchCryptoDataFromAPI();
        verify(spyService, never()).getMockCryptos();
        assertEquals(apiData, result);
    }

    //TODO REVISAR EL TEST
    @Test
    void getMockCryptos_ShouldReturnArticles() throws IOException {


//        List<Article> existingArticles = List.of(mockArticle, mockArticle2);
        List<Article> existingArticles = new ArrayList<Article>();
        when(articleRepository.findAll()).thenReturn(existingArticles);
        when(articleRepository.save(any())).thenReturn(
        new Article("bitcoin", "btc", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", 63562, new BigInteger("850000000000"), new BigInteger("50000000000"), 46000, 44000, -1000, -2.17, new BigInteger("10000000000"), -1.17, new BigInteger("18000000"), new BigInteger("21000000"), 69000, -34.83, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"),
        new Article("ethereum", "eth", "https://assets.coingecko.com/coins/images/279/large/ethereum.png", 3000, new BigInteger("350000000000"), new BigInteger("20000000000"), 3200, 2900, -100, -3.23, new BigInteger("5000000000"), -1.42, new BigInteger("115000000"), new BigInteger("120000000"), 4800, -37.5, "2021-11-10T00:00:00Z", "2024-10-12T20:36:50Z"),
        new Article("ripple", "xrp", "https://assets.coingecko.com/coins/images/44/large/ripple.png", 0.80, new BigInteger("40000000000"), new BigInteger("1500000000"), 0.85, 0.75, -0.02, -2.44, new BigInteger("500000000"), -1.25, new BigInteger("50000000000"), new BigInteger("100000000000"), 3.84, -79.17, "2018-01-07T00:00:00Z", "2024-10-12T20:36:50Z"),
        new Article("litecoin", "ltc", "https://assets.coingecko.com/coins/images/2/large/litecoin.png", 150, new BigInteger("10000000000"), new BigInteger("500000000"), 160, 140, -5, -3.23, new BigInteger("300000000"), -2.92, new BigInteger("84000000"), new BigInteger("84000000"), 400, -62.5, "2021-05-10T00:00:00Z", "2024-10-12T20:36:50Z"),
        new Article("cardano", "ada", "https://assets.coingecko.com/coins/images/975/large/cardano.png", 0.50, new BigInteger("16000000000"), new BigInteger("700000000"), 0.55, 0.48, -0.02, -3.85, new BigInteger("200000000"), -1.23, new BigInteger("32000000000"), new BigInteger("450000000"), 3.10, -83.87, "2021-09-02T00:00:00Z", "2024-10-12T20:36:50Z")
        );





        List<Article> result = articleService.getMockCryptos();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("bitcoin", result.get(0).getSymbol());
        assertEquals("ethereum", result.get(1).getSymbol());
        assertEquals("ripple", result.get(2).getSymbol());
        assertEquals("litecoin", result.get(3).getSymbol());
        assertEquals("cardano", result.get(4).getSymbol());

        // Verificar que el método findAll() se llamó una vez
        verify(articleRepository, times(1)).findAll();
        verify(articleRepository, times(5)).save(any());
    }


}