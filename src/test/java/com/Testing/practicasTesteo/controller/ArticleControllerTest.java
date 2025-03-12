package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    private Article article;
    private List<Article> articles;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .articleId(1L)
                .symbol("bitcoin")
                .name("btc")
                .currentPrice(63562)

                .build();

        articles = Arrays.asList(article);
    }

    @Test
    void getAllArticles_ShouldReturnOk() throws Exception {
        when(articleService.getAllArticles()).thenReturn(articles);

        mockMvc.perform(get("/article/getAllArticles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].symbol").value("bitcoin"));
    }

    @Test
    void getAllArticles_ShouldReturnNotFound() throws Exception {
        when(articleService.getAllArticles()).thenThrow(new ArticleNotFoundException("Articles not found"));

        mockMvc.perform(get("/article/getAllArticles"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Articles not found"));

    }

    @Test
    void getArticleById_ShouldReturnOk() throws Exception {
        when(articleService.getArticleById(1L)).thenReturn(article);

        mockMvc.perform(get("/article/getArticleById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("bitcoin"));
    }

    @Test
    @DisplayName("GET /article/getArticlesById/1 - Should return 404 Not Found when customer has no credits")
    void getArticleById_ShouldReturnNotFound() throws Exception {

        when(articleService.getArticleById(1L))
                .thenThrow(new ArticleNotFoundException("Article not found with id: " + 1L));

        mockMvc.perform(get("/article/getArticleById/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Article not found with id: 1"));

    }

    @Test
    void saveArticle_ShouldReturnCreated() throws Exception {
        when(articleService.saveArticle(any(Article.class))).thenReturn(article);

        String articleJson = """
                {
                    "symbol": "bitcoin",
                    "name": "btc",
                    "currentPrice": "63562"
                }
                """;

        mockMvc.perform(post("/article/saveArticle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("bitcoin"));
    }

    @Test
    void saveArticle_ShouldReturnBadRequest() throws Exception {
        when(articleService.saveArticle(any(Article.class))).thenThrow(NotSavedException.class);

        String articleJson = """
                {
                    "symbol": "bitcoin",
                    "name": "btc",
                    "currentPrice": "63562"
                }
                """;

        mockMvc.perform(post("/article/saveArticle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAllArticles_ShouldReturnOk() throws Exception {
        when(articleService.deleteAllArticles()).thenReturn(true);

        mockMvc.perform(delete("/article/deleteAllArticles"))
                .andExpect(status().isOk())
                .andExpect(content().string("Se han eliminado todos los articulos"));
    }

    @Test
    void deleteAllArticles_ShouldReturnNotFound() throws Exception {
        when(articleService.deleteAllArticles()).thenReturn(false);

        mockMvc.perform(delete("/article/deleteAllArticles"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se han eliminado los articulos"));
    }



    @Test
    void getArticlesByCustomerId_ShouldReturnOk() throws Exception {
        when(articleService.getArticlesByCustomerId(1L)).thenReturn(articles);

        mockMvc.perform(get("/article/getArticlesByCustomerId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].symbol").value("bitcoin"));
    }

    @Test
    void getArticlesByCustomerId_ShouldReturnNotFound() throws Exception {
        when(articleService.getArticlesByCustomerId(anyLong())).thenThrow(ArticleNotFoundException.class);

        mockMvc.perform(get("/article/getArticlesByCustomerId/1"))
                .andExpect(status().isNotFound());
    }
}
