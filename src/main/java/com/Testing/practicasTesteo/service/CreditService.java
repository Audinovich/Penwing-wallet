package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CreditService {
    List<Credit> getAllCreditsByCustomerId(Long customerId) throws ArticleFetchException;

    Credit updateCryptoBalance(Long customerId, BigDecimal amount, String creditType, String operation) throws IOException;

    List<ArticleCreditDTO> getArticleCreditInfoByCustomerId(Long customerId) throws ArticleFetchException;

    Credit addEuroCredit(Long customerId, BigDecimal amount);
}
