package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ArticleServiceImplTest {


    @MockBean
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    private Article mockArticle;


    @BeforeEach
    void setUp() {
        mockArticle = Article.builder()
                .articleId(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .currentPrice(50000.0)
                .marketCap(BigInteger.valueOf(1000000000))

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
    void getArticleById() {
    }

    @Test
    void updateArticleById() {
    }

    @Test
    void saveArticle() {
    }

    @Test
    void saveArticles() {
    }

    @Test
    void deleteAllArticles() {
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