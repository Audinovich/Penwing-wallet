package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("Articles")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @GetMapping("/getAllArticles")
    public ResponseEntity<List<Article>> getAllArrticles() {
        List<Article> articlesFound = articleService.getAllArticles();
        if (articlesFound.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/getArticleById/{articleId}")
    public ResponseEntity<Optional<Article>>getArticleById(@PathVariable("articleId") long articleId){
        Optional<Article> articleFoundById = articleService.getArticleByid(articleId);
        return articleFoundById.map(value -> new ResponseEntity<>(Optional.of(value),HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(Optional.empty(), HttpStatus.NOT_FOUND));
    }

    @PutMapping("/updateArticle/{id}")
    public ResponseEntity<Optional<Article>> updateArticleById(@RequestBody Article article, @PathVariable("id") long id) {
        try {
            Optional<Article> articleUpdated = articleService.updateArticleById(article, id);
            return articleUpdated.map(value -> new ResponseEntity<>(Optional.of(value), HttpStatus.OK))
                    .orElseGet(() ->new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }







}
