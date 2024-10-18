package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository <Article, Long> {
    @Query("SELECT a FROM Article a JOIN a.wallets w JOIN w.customer c WHERE c.customerId = :customerId")
    List<Article> getAllArticlesByCustomerId(Long customerId);

}
