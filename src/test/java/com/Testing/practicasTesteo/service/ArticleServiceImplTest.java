package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
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

        assertEquals("Article not found with id: 1", articleException.getMessage());
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
        assertEquals("An unexpected error occurred: Error",exception.getMessage());
        verify(articleRepository,times(1)).deleteById(1L);

    }

    @Test
    void deleteAllArticlesShouldDeleteArticlesSuccessfully() {

        List<Article> articlesList = List.of(mockArticle,mockArticle2);
        articleRepository.deleteAll(articlesList);

        verify(articleRepository,times(1)).deleteAll(articlesList);

    }
    @Test
    void deleteAllArticlesShouldThrowNotDeletedException_WhenRuntimeExceptionOccurs() {

        doThrow(new RuntimeException("Error deleting Articles."))
                .when(articleRepository).deleteAll();

        NotDeletedException exception = assertThrows(NotDeletedException.class,()->{
            articleService.deleteAllArticles();

        });
        assertEquals("Articles not deleted: Error deleting Articles.",exception.getMessage());

        verify(articleRepository,times(1)).deleteAll();
    }

    @Test
    void deleteAllArticlesShouldReturnNotDeletedException_WhenExceptionOccurs() {

        // TODO REVISAR ANOTACION INVOCATION O INVOCATION ON MOCK.
        doAnswer(invocationOnMock ->{throw  new Exception("Internal Error");})
                .when(articleRepository).deleteAll();
        ;
        NotDeletedException exception = assertThrows(NotDeletedException.class,()->{
            articleService.deleteAllArticles();
        });

        assertEquals("An unexpected error occurred: Internal Error",exception.getMessage());

        verify(articleRepository,times(1)).deleteAll();

    }



    @Test
    void deleteArticleById() {
    }

    @Test
    void fetchCryptoData() {
    }

    @Test
    void getArticlesByCustomerId() {
    }

    @Test
    void getMockCryptos() {
    }
}