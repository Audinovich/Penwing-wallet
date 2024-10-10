package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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
    public Article updateArticleById(Article a, long id) throws ArticleNotFoundException {

        Article articleUpdated = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article ID " + id + " Not Found."));

        articleUpdated.setCodigo(a.getCodigo());
        articleUpdated.setDescripcion(a.getDescripcion());
        articleUpdated.setPrecio(a.getPrecio());

        return articleRepository.save(articleUpdated);
    }



    @Override
    public Article saveArticle(Article article) {
        try {
            return articleRepository.save(article);
        } catch (Exception e) {
            throw new NotSavedException("Error saving article"  + e.getMessage(), e);
        }
    }

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
}
