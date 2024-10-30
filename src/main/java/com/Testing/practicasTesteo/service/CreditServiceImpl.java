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

import java.io.IOException;
import java.math.BigDecimal;
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

    @Autowired
    ArticleServiceImpl articleServiceimpl;


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
                creditMap.merge("cardano", credit.getCardano() != null ? credit.getCardano(): 0.0, Double::sum);
                creditMap.merge("euro", credit.getEuro() != null ? credit.getEuro() : 0.0, Double::sum);
            }

            List<ArticleCreditDTO> articleCreditDTOs = new ArrayList<>();

            for (Article article : articles) {
                ArticleCreditDTO dto = new ArticleCreditDTO();
                dto.setSymbol(article.getSymbol());
                dto.setName(article.getName());
                dto.setImage(article.getImage());
                dto.setCurrentPrice(article.getCurrentPrice());


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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Credit addEuroCredit(Long customerId, Double amount) {

        Credit credit = creditRepository.findMoneyByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Credit not found for customer with ID: " + customerId));

        if (credit.getEuro() == null) {
            credit.setEuro(0.0);
        }
        credit.setEuro(credit.getEuro() + amount);

        return creditRepository.save(credit);
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
    public Credit updateCryptoBalance(Long customerId, Double amount, String creditType, String operation) throws IOException {
        // Buscamos el crédito del cliente
        Credit credit = creditRepository.findMoneyByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Credit not found for customer with ID: " + customerId));

        // Obtener el precio de la criptomoneda desde la API mock
        Double price = fetchCryptoPriceFromMockAPI(creditType);

        // Lógica para actualizar el balance de criptomonedas o euros
        switch (operation.toLowerCase()) {
            case "buy":
                // Verifica si hay suficiente saldo en euros
                if (credit.getEuro().compareTo(amount * price) < 0) {
                    throw new IllegalArgumentException("Not enough euros to complete the purchase.");
                }
                // Descontar euros y actualizar balance de la criptomoneda específica
                credit.setEuro(credit.getEuro() - (amount * (price))); // Cambiado a BigDecimal
                updateSpecificCryptoBalance(credit, creditType, amount, true); // Aumentar cantidad
                break;

            case "sell":
                // Verifica si hay suficiente saldo en la criptomoneda específica
                Double currentCryptoBalance = getSpecificCryptoBalance(credit, creditType);// Convertir a BigDecimal
                if (currentCryptoBalance.compareTo(amount) < 0) { // Comparar ambos como BigDecimal
                    throw new IllegalArgumentException("Not enough crypto to complete the sale.");
                }
                // Aumentar euros y reducir el balance de la criptomoneda específica
                credit.setEuro(credit.getEuro()+(amount*(price)));
                updateSpecificCryptoBalance(credit, creditType, amount, false); // Reducir cantidad
                break;

            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }

        return creditRepository.save(credit);
    }

    public Double fetchCryptoPriceFromMockAPI(String creditType) throws IOException {
        List<Article> mockCryptos = articleServiceimpl.getMockCryptos();

        for (Article article : mockCryptos) {
            if (article.getSymbol().equalsIgnoreCase(creditType)) {
                return (article.getCurrentPrice());
            }
        }
        throw new IllegalArgumentException("No se encontró una criptomoneda con el tipo especificado: " + creditType);
    }

    // Métodos auxiliares para actualizar y obtener el balance de una criptomoneda específica
    private Double getSpecificCryptoBalance(Credit credit, String cryptoType) {
        return switch (cryptoType.toLowerCase()) {
            case "bitcoin" -> credit.getBitcoin() != null ? credit.getBitcoin() : 0.0;
            case "ethereum" -> credit.getEthereum() != null ? credit.getEthereum() : 0.0;
            case "ripple" -> credit.getRipple() != null ? credit.getRipple() : 0.0;
            case "litecoin" -> credit.getLitecoin() != null ? credit.getLitecoin() : 0.0;
            case "cardano" -> credit.getCardano() != null ? credit.getCardano() : 0.0;
            default -> throw new IllegalArgumentException("Invalid cryptocurrency type: " + cryptoType);
        };
    }

    private void updateSpecificCryptoBalance(Credit credit, String cryptoType, Double amount, boolean isBuyOperation) {
        Double currentBalance = getSpecificCryptoBalance(credit, cryptoType);
        Double updatedBalance = isBuyOperation ? currentBalance + (amount) : currentBalance - (amount);

        switch (cryptoType.toLowerCase()) {
            case "bitcoin" -> credit.setBitcoin(updatedBalance);
            case "ethereum" -> credit.setEthereum(updatedBalance);
            case "ripple" -> credit.setRipple(updatedBalance);
            case "litecoin" -> credit.setLitecoin(updatedBalance);
            case "cardano" -> credit.setCardano(updatedBalance);
            default -> throw new IllegalArgumentException("Invalid cryptocurrency type: " + cryptoType);
        }
    }


}
