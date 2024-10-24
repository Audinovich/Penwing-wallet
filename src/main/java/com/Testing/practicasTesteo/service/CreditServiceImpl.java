package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.respository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    CreditRepository creditRepository;

    @Autowired
    ArticleService articleService;


    @Override
    public List<ArticleCreditDTO> getArticleCreditInfoByCustomerId(Long customerId) throws ArticleFetchException {
        try {
            // Obtener créditos del cliente
            List<Credit> credits = creditRepository.getAllCreditsByCustomerId(customerId);
            List<Article> articles = articleService.getAllArticles();


            Map<String, Double> creditMap = new HashMap<>();


            for (Credit credit : credits) {
                creditMap.merge("bitcoin", credit.getBitcoin() != null ? credit.getBitcoin() : 0.0, Double::sum);
                creditMap.merge("ethereum", credit.getEthereum() != null ? credit.getEthereum() : 0.0, Double::sum);
                creditMap.merge("ripple", credit.getRipple() != null ? credit.getRipple() : 0.0, Double::sum);
                creditMap.merge("litecoin", credit.getLitecoin() != null ? credit.getLitecoin() : 0.0, Double::sum);
                creditMap.merge("cardano", credit.getCardano() != null ? credit.getCardano() : 0.0, Double::sum);
                creditMap.merge("euro", credit.getEuro() != null ? credit.getEuro().doubleValue() : 0.0, Double::sum);
            }

            List<ArticleCreditDTO> articleCreditDTOs = new ArrayList<>();


            for (Article article : articles) {
                ArticleCreditDTO dto = new ArticleCreditDTO();
                dto.setSymbol(article.getSymbol());
                dto.setName(article.getName());
                dto.setImage(article.getImage());

                // Obtener el crédito correspondiente del mapa
                Double creditAmount = creditMap.getOrDefault(article.getSymbol().toLowerCase(), 0.0);
                dto.setCreditAmount(creditAmount);

                articleCreditDTOs.add(dto);
            }

            return articleCreditDTOs;

        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing credit data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para capitalizar el nombre del símbolo (por ejemplo, bitcoin -> Bitcoin)
    private String capitalize(String symbol) {
        return symbol.substring(0, 1).toUpperCase() + symbol.substring(1);
    }


    @Override
    public List<Credit> getAllCreditsByCustomerId(Long customerId) throws ArticleFetchException, ArticleNotFoundException {
        try {
            List<Credit> creditsFound = creditRepository.getAllCreditsByCustomerId(customerId);
            if (creditsFound.isEmpty()) {
                throw new ArticleNotFoundException("No credit found");
            }
            return creditsFound;
        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing credit data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override

    @Transactional(rollbackFor = Exception.class)
    public Credit addCredit(Long customerId, Long amount) {

        Credit credit = creditRepository.findMoneyByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Credit not found for customer with ID: " + customerId));


        if (credit.getEuro() == null) {
            credit.setEuro(0L);
        }
        credit.setEuro(credit.getEuro() + amount);

        return creditRepository.save(credit);
    }
}
