package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.dto.ArticleCreditDTO;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotFoundException;
import com.Testing.practicasTesteo.service.CreditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditController.class)
class CreditControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private CreditService creditService;

    private Credit credit;
    private List<Credit> credits;

    @BeforeEach
    void setUp() {

        credit = Credit.builder()
                .creditId(1L)
                .bitcoin(1.0)
                .euro(5000.0)
                .ethereum(3.0)
                .build();
        credits = Arrays.asList(credit);
    }

    @Test
    void getAllCreditsByCustomerId_ShouldReturnOk() throws Exception {

        when(creditService.getAllCreditsByCustomerId(1L)).thenReturn(credits);

        mockMvc.perform(get("/credit/getAllCreditsByCustomerId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].creditId").value(1))
                .andExpect(jsonPath("$[0].bitcoin").value(1.0))
                .andExpect(jsonPath("$[0].euro").value(5000.0))
                .andExpect(jsonPath("$[0].ethereum").value(3.0));
    }

    @Test
    @DisplayName("GET /credit/getAllCreditsByCustomerId/1 - Should return 404 Not Found when customer has no credits")
    void getAllCreditsByCustomerId_ShouldReturnNotFound() throws Exception {

        when(creditService.getAllCreditsByCustomerId(1L))
                .thenThrow(new CustomerNotFoundException("Credit not found for customer with ID: " + 1L));

        mockMvc.perform(get("/credit/getAllCreditsByCustomerId/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Credit not found for customer with ID: " + 1L));
    }


    @Test
    @DisplayName("GET /credit/getAllCreditsByCustomerId/1 - Should return 500 Internal Server Error")
    void getAllCreditsByCustomerId_ShouldReturnInternalServerError() throws Exception {

        when(creditService.getAllCreditsByCustomerId(1L))
                .thenThrow(new RuntimeException("Unexpected error"));


        mockMvc.perform(get("/credit/getAllCreditsByCustomerId/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Unexpected error")));
    }



    @Test
    void addCredit_ShouldReturnInternalServerError() throws Exception {
        when(creditService.addEuroCredit(eq(1L), any(Double.class)))
                .thenThrow(new RuntimeException("Internal Server Error"));

        String requestBody = """
                {
                    "amount": 500.0
                }
                """;
        mockMvc.perform(put("/credit/addEuroCredit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());

    }

    //TODO revisar
    @Test
    void addCredit_ShouldReturnOk() throws Exception {
        Credit credit = Credit.builder().creditId(1L).euro(1500.0).build();
        when(creditService.addEuroCredit(eq(1L), any(Double.class)))
                .thenAnswer(invocation -> {
                    Double amount = invocation.getArgument(1);
                    credit.setEuro(credit.getEuro() + amount);
                    return credit;
                });

        String requestBody = """
                {
                    "amount": 500.0
                }
                """;
        mockMvc.perform(put("/credit/addEuroCredit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditId").value(1))
                .andExpect(jsonPath("$.euro").value(2000.0));
    }

    @Test
    void addCredit_ShouldReturnNotFound() throws Exception {
        // Simula el comportamiento del servicio cuando no encuentra el crédito
        when(creditService.addEuroCredit(eq(1L), any(Double.class)))
                .thenThrow(new NotFoundException("Credit not found for customer with ID: " + 1L));

        // Cuerpo JSON de la solicitud
        String requestBody = """
                {
                    "amount": 500.0
                }
                """;

        // Realiza la solicitud PUT simulada
        mockMvc.perform(put("/credit/addEuroCredit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /credit/getArticleCreditInfoShouldReturnOk")
    void getArticleCreditInfoShouldReturnOk() throws Exception {

        //creo la List de DTOs mockArticleCredits, con un solo elemento.
        List<ArticleCreditDTO> mockArticleCredits = List.of(
                new ArticleCreditDTO("bitcoin", "btc", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", 6.0, 63562.0, 609628.0)
        );

        when(creditService.getArticleCreditInfoByCustomerId(1L)).thenReturn(mockArticleCredits);

        mockMvc.perform(get("/credit/articleCreditInfo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("btc"))
                .andExpect(jsonPath("$[0].symbol").value("bitcoin"))
                .andExpect(jsonPath("$[0].creditAmount").value(6.0))
                .andExpect(jsonPath("$[0].image").value("https://assets.coingecko.com/coins/images/1/large/bitcoin.png"))
                .andExpect(jsonPath("$[0].currentPrice").value(63562.0))
                .andExpect(jsonPath("$[0].euroBalance").value(609628.0));
    }

    @Test
    @DisplayName("PUT /credit/updateCryptoBalanceShouldReturnOk")
    void updateCryptoBalanceShouldReturnOk()throws Exception {

        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("creditType","bitcoin");
        requestBody.put("amount",5.0);
        requestBody.put("operation","buy");

        Credit mockCredit = new Credit();
        mockCredit.setBitcoin(6.0);

        when(creditService.updateCryptoBalance (eq(1L), eq(5.0), eq("bitcoin"), eq("buy")))
                .thenReturn(mockCredit);

        mockMvc.perform(put("/credit/updateCryptoBalance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bitcoin").value(6.0));

    }

    @Test
    @DisplayName("PUT /credit/updateCryptoBalanceShouldReturnNotFound")
    void updateCryptoBalanceShouldReturnNotFound()throws Exception {

        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("creditType","bitcoin");
        requestBody.put("amount",5.0);
        requestBody.put("operation","buy");

        Credit mockCredit = new Credit();
        mockCredit.setBitcoin(6.0);

        when(creditService.updateCryptoBalance (eq(1L), eq(5.0), eq("bitcoin"), eq("buy")))
                .thenThrow(new NotFoundException("Credit not found for customer with ID:" + 1L));

        mockMvc.perform(put("/credit/updateCryptoBalance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Credit not found for customer with ID:" + 1L)));

    }

    @Test
    @DisplayName("PUT /credit/updateCryptoBalanceShouldReturnInternalServerError")
    void updateCryptoBalanceShouldReturnInternalServerError()throws Exception {

        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("creditType","bitcoin");
        requestBody.put("amount",5.0);
        requestBody.put("operation","buy");

        Credit mockCredit = new Credit();
        mockCredit.setBitcoin(6.0);

        when(creditService.updateCryptoBalance (eq(1L), eq(5.0), eq("bitcoin"), eq("buy")))
                .thenThrow(new Exception("Credit not found for customer with ID:" + 1L));

        mockMvc.perform(put("/credit/updateCryptoBalance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Credit not found for customer with ID:" + 1L)));

    }
}