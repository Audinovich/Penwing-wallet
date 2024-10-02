package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository <Article, Long> {
}
