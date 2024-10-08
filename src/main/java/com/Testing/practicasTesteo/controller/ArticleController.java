package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Articles")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @GetMapping("/getAllArticles")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articlesFound = articleService.getAllArticles();
        if (articlesFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/getArticleById/{articleId}")
    public ResponseEntity<Article> getArticleById(@PathVariable("articleId") long articleId) {
        try {
            Article articleFoundById = articleService.getArticleByid(articleId);
            return new ResponseEntity<>(articleFoundById, HttpStatus.OK);
        } catch (ArticleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saveArticle")
    public ResponseEntity<Article> saveArticle(@RequestBody Article article) throws NotSavedException {
        try {
            Article savedArticle = articleService.saveArticle(article);
            return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
        } catch (NotSavedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //TODO POR QUE MAP? en caso de manejar excepciones puedo obviar el uso de Optional.
    @PutMapping("/updateArticle/{id}")
    public ResponseEntity<Article> updateArticleById(@RequestBody Article article, @PathVariable("id") long id) {
        try {
            Article articleUpdated = articleService.updateArticleById(article, id);
            return new ResponseEntity<>(articleUpdated, HttpStatus.OK);
        } catch (ArticleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllArticles")
    public ResponseEntity<String> deleteAllArticles() {
        try {
            boolean articleFound = articleService.deleteAllArticles();
            if (articleFound) {
                return new ResponseEntity<>("Se han eliminado todos los articulos", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se han eliminado los articulos", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping("/deleteArticleById/{id}")
    public ResponseEntity<String> deleteArticleById(@PathVariable("id") Long id) {
        try {
            boolean articleDeleted = articleService.deleteArticleById(id);
            if (articleDeleted) {
                return new ResponseEntity<>("Se ha eliminado el articulo", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se ha eliminado el articulo", HttpStatus.NOT_FOUND);
            }
        } catch (ArticleNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
