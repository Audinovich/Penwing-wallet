package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.ArticleFetchException;
import com.Testing.practicasTesteo.exceptions.ArticleNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.respository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    CreditRepository creditRepository;

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
