package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.respository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditServiceImpl implements CreditService {


   private final CreditRepository creditRepository;

   public CreditServiceImpl(CreditRepository creditRepository){
       this.creditRepository= creditRepository;
   }

    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleServiceImpl articleServiceimpl;

    //TODO REVISAR ESTE METODO PARA DEJAR CLARISIMA LA LOGICA
    @Override
    public List<ArticleCreditDTO> getArticleCreditInfoByCustomerId(Long customerId) throws ArticleFetchException {
        //PARA RESOLVER POSIBLES EXCEPCIONES
        try {
            // Obtener créditos del cliente
            List<Credit> credits = creditRepository.getAllCreditsByCustomerId(customerId);
            List<Article> articles = articleService.getAllArticles();

            // SE CREA UN ARTICLECREDITMAP = AL METODO AUXILIAR QUE YA TIENE UN NEW Y SE LE PASAN LOS CREDITOS COMO PARAMETRO por que asi lo exige
            Map<String, Double> articleCreditMap = buildCreditMap(credits);

            List<ArticleCreditDTO> articleCreditDTOs = new ArrayList<>();

            Double euroBalance = credits.isEmpty() ? 0.0 : credits.get(0).getEuro();

            for (Article article : articles) {
                ArticleCreditDTO dto = new ArticleCreditDTO();
                dto.setSymbol(article.getSymbol());
                dto.setName(article.getName());
                dto.setImage(article.getImage());
                dto.setCurrentPrice(article.getCurrentPrice());


                // Obtener el crédito correspondiente del mapa // buscando por el nombre de la lsitade creditmap y asignando un valor
                Double creditAmount = articleCreditMap.getOrDefault(article.getSymbol().toLowerCase(), 0.0);
                dto.setCreditAmount(creditAmount);
                dto.setEuroBalance(euroBalance);
                articleCreditDTOs.add(dto);
            }

            return articleCreditDTOs;

        } catch (DataAccessException e) {
            throw new ArticleFetchException("Error accessing credit data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // Método AUXILIAR para construir el creditMap y le asigna
    private Map<String, Double> buildCreditMap(List<Credit> credits) {
        //devuelve un objeto de tipo MAP
        Map<String, Double> creditMap = new HashMap<>();
        //cryptos/creditos

        //rellena los campos valor con los credit para cada una de las crypto
        if (credits != null) {
            for (Credit credit : credits) {
                creditMap.put("bitcoin", credit.getBitcoin() != null ? credit.getBitcoin() : 0.0);
                creditMap.put("ethereum", credit.getEthereum() != null ? credit.getEthereum() : 0.0);
                creditMap.put("ripple", credit.getRipple() != null ? credit.getRipple() : 0.0);
                creditMap.put("litecoin", credit.getLitecoin() != null ? credit.getLitecoin() : 0.0);
                creditMap.put("cardano", credit.getCardano() != null ? credit.getCardano() : 0.0);
                creditMap.put("euro", credit.getEuro() != null ? credit.getEuro() : 0.0);
            }
        }

        return creditMap;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Credit addEuroCredit(Long customerId, Double amount) {
        try {
            Credit credit = creditRepository.findMoneyByCustomerId(customerId)
                    .orElseThrow(() -> new NotFoundException("Credit not found for customer with ID: " + customerId));

            if (credit.getEuro() == null) {
                credit.setEuro(0.0);
            }
            credit.setEuro(credit.getEuro() + amount);

            return creditRepository.save(credit);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }


    @Override
    public List<Credit> getAllCreditsByCustomerId(Long customerId) throws ArticleFetchException {
        try {
            List<Credit> creditsFound = creditRepository.getAllCreditsByCustomerId(customerId);
            if (creditsFound.isEmpty()) {
                throw new CustomerNotFoundException("No credit found for customer with ID: " + customerId);
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

        Credit credit = creditRepository.findMoneyByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Credit not found for customer with ID: " + customerId));


        Double price = fetchCryptoPriceFromMockAPI(creditType);


        switch (operation.toLowerCase()) {
            case "buy":

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
                credit.setEuro(credit.getEuro() + (amount * (price)));
                updateSpecificCryptoBalance(credit, creditType, amount, false);
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
