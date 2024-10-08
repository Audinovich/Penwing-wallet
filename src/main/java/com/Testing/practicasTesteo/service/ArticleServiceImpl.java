package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleRepository articleRepository;


    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleByid(long id) throws ArticleNotFoundException {
        return articleRepository.findById(id);
    }

    @Override
    public Optional<Article> updateArticleById(Article a, long id) {
        Optional<Article> articleFound = articleRepository.findById(id);
        if (articleFound.isPresent()){
            Article articleUpdated = articleFound.get();
            articleUpdated.setCodigo(a.getCodigo());
            articleUpdated.setDescripcion(a.getDescripcion());
            articleUpdated.setPrecio(a.getPrecio());

            return Optional.of(articleRepository.save(articleUpdated));

        } else {
            throw new ArticleNotFoundException("Article ID  " + id + "no encontrado.");
        }
    }

    @Override
    public Article saveArticle(Article a) {
        return articleRepository.save(a);
    }

    @Override
    public boolean deleteAllArticles() {

        try {
            articleRepository.deleteAll();
            return true;
        } catch (Exception e) {
            throw new ArticleNotFoundException("Articles not Found.");
        }
    }

    @Override
    public boolean deleteArticleById(long id) {
        try {
            Optional<Article> articleFound = articleRepository.findById(id);
            articleRepository.delete(articleFound.get());
            return true;

        } catch (Exception e) {
            throw new ArticleNotFoundException("Articles not Found. ");
        }
    }
}
