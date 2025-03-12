package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;

import java.io.IOException;
import java.util.List;

public interface ArticleService {

    List<Article> getAllArticles() throws ArticleFetchException;
    Article getArticleById(long id) throws ArticleNotFoundException;
    Article updateArticleById(Article a, long id);
    Article saveArticle(Article article);
    List<Article> saveArticles(List<Article> articles);
    boolean deleteAllArticles();
    boolean deleteArticleById(long id);
    List<Article> getMockCryptos() throws IOException;
    List<Article> fetchCryptoDataFromAPI() throws IOException;;
    List<Article> getArticlesByCustomerId(Long customerId) throws ArticleFetchException;
}
