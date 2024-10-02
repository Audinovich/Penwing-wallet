package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

    List<Article> getAllArticles();
    Optional<Article> getArticleByid(long id) throws ArticleNotFoundException;
    Optional<Article> updateArticleById(Article a, long id);
    Article saveArticle(Article a);
    boolean deleteAllArticles();
    boolean deleteArticleById(long id);

}
