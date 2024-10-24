package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CreditService {
    List<Credit> getAllCreditsByCustomerId(Long customerId) throws ArticleFetchException;
    Credit addCredit(Long customerId,Long amount);
    List<ArticleCreditDTO> getArticleCreditInfoByCustomerId(Long customerId) throws ArticleFetchException;

}
