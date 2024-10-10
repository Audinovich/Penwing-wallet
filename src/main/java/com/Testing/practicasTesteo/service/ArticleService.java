package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

    List<Article> getAllArticles() throws ArticleFetchException;
    Article getArticleById(long id) throws ArticleNotFoundException;
    Article updateArticleById(Article a, long id);
    Article saveArticle(Article a);
    boolean deleteAllArticles();
    boolean deleteArticleById(long id);

}
