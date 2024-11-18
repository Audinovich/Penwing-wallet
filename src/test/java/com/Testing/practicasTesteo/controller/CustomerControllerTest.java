package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    private Customer customer;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .customerId(1L)
                .name("juan")
                .surname("perez")
                .email("juanperez@gmail.com")
                .address("calle1")
                .phone("+34657890890")
                .build();

        customers = Arrays.asList(customer);
    }

    @Test
    @DisplayName("GET /customer/getAllCustomers - Should return 200-OK")
    void getAllCustomersShouldReturnO()throws Exception {
      when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/customer/getAllCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].name").value("juan"))
                .andExpect(jsonPath("$[0].surname").value("perez"))
                .andExpect(jsonPath("$[0].email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$[0].address").value("calle1"))
                .andExpect(jsonPath("$[0].phone").value("+34657890890"));
    }

    @Test
    @DisplayName("GET /customer/getAllCustomers - Should return 404-OK")
    void getAllCustomersShouldReturnNotFound()throws Exception {
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/customer/getAllCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].name").value("juan"))
                .andExpect(jsonPath("$[0].surname").value("perez"))
                .andExpect(jsonPath("$[0].email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$[0].address").value("calle1"))
                .andExpect(jsonPath("$[0].phone").value("+34657890890"));
    }



    @Test
    @DisplayName("GET /customer/getCustomerById/1 - Should return 200-OK")
    void getCustomerById()throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        mockMvc.perform(get("/customer/getCustomerById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.name").value("juan"))
                .andExpect(jsonPath("$.surname").value("perez"))
                .andExpect(jsonPath("$.email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$.address").value("calle1"))
                .andExpect(jsonPath("$.phone").value("+34657890890"));

    }

    @Test
    void saveCustomer() {
    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteAllCustomers() {
    }

    @Test
    void deleteCustomerById() {
    }
}