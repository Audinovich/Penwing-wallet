package com.Testing.practicasTesteo.controller;

import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void getAllCustomersShouldReturnO() throws Exception {
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
    @DisplayName("GET /customer/getAllCustomers - Should return 404-Not Found")
    void getAllCustomersShouldReturnNotFound() throws Exception {
        when(customerService.getAllCustomers()).thenThrow(new CustomerNotFoundException("Customers not found"));

        mockMvc.perform(get("/customer/getAllCustomers"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customers not found"));
    }

    @Test
    @DisplayName("GET /customer/getAllCustomers - Should return 500 -Not Found")
    void getAllCustomersShouldReturnInternalServerError() throws Exception {
        when(customerService.getAllCustomers()).thenThrow(new RuntimeException("Failed to get Customers"));

        mockMvc.perform(get("/customer/getAllCustomers"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to get Customers"));
    }


    @Test
    @DisplayName("GET /customer/getCustomerById/1 - Should return 200-OK")
    void getCustomerById() throws Exception {
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
    @DisplayName("GET /customer/getCustomerById/1 - Should return 404 -Not Found")
    void getCustomerByIdShouldReturnNotFound() throws Exception {

        when(customerService.getCustomerById(1L))
                .thenThrow(new CustomerNotFoundException("Customers not found whit Id:" + 1L));

        mockMvc.perform(get("/customer/getCustomerById/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customers not found whit Id:" + 1L));
    }

    @Test
    @DisplayName("GET /customer/getCustomerById/1 - Should return 500 -Not Found")
    void getCustomerByIdShouldReturnInternalServerError() throws Exception {

        when(customerService.getCustomerById(1L))
                .thenThrow(new RuntimeException("Failed to get Customer"));

        mockMvc.perform(get("/customer/getCustomerById/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to get Customer"));
    }

    @Test
    @DisplayName("POST /customer/saveCustomer - Should return 201 -Created")
    void saveCustomerShouldReturnOK() throws Exception {

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(post("/customer/saveCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("juan"))
                .andExpect(jsonPath("$.surname").value("perez"))
                .andExpect(jsonPath("$.email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$.address").value("calle1"))
                .andExpect(jsonPath("$.phone").value("+34657890890"));

    }

    @Test
    @DisplayName("POST /customer/saveCustomer - Should return NotSavedException ")
    void saveCustomerShuoudReturnNotSavedException() throws Exception {

        when(customerService.saveCustomer(any(Customer.class))).thenThrow(new NotSavedException("Failed to save customer data: ") {
        });

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(post("/customer/saveCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isNotImplemented())
                .andExpect(content().string("Failed to save customer data: "));

    }


    @Test
    @DisplayName("POST /customer/saveCustomer - Should return 500-RuntimeException ")
    void saveCustomerShuoudReturnRuntimeException() throws Exception {

        when(customerService.saveCustomer(any(Customer.class))).thenThrow(new RuntimeException("Unexpected error while saving customer: ") {
        });

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(post("/customer/saveCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error while saving customer: "));

    }

    @Test
    @DisplayName("PUT /customer/updateCustomer/1 - Should Return OK")
    void updateCustomerShouldReturnOk() throws Exception {

        when(customerService.updateCustomerById(any(Customer.class), eq(1L)))
                .thenReturn(customer);

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(put("/customer/updateCustomer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("juan"))
                .andExpect(jsonPath("$.surname").value("perez"))
                .andExpect(jsonPath("$.email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$.address").value("calle1"))
                .andExpect(jsonPath("$.phone").value("+34657890890"));

    }

    @Test
    @DisplayName("PUT /customer/updateCustomer/1 - Should Return - 500 Internal Server Error")
    void updateCustomerShouldReturnInternalServerError() throws Exception {

        when(customerService.updateCustomerById(any(Customer.class), eq(1L)))
                .thenThrow(new RuntimeException());

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(put("/customer/updateCustomer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isInternalServerError());


    }

    @Test
    @DisplayName("PUT /customer/updateCustomer/1 - Should Return 404- Not Found")
    void updateCustomerShouldReturnNotFound() throws Exception {

        when(customerService.updateCustomerById(any(Customer.class), eq(1L)))
                .thenThrow(new CustomerNotFoundException("Customer not found whit Id:" + 1L));

        String customerJson = """
                {
                    "name": "juan",
                    "surname": "perez",
                    "email": "juanperez@gmail.com",
                    "address": "calle1",
                    "phone":"+34657890890"
                             
                }
                """;

        mockMvc.perform(put("/customer/updateCustomer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found whit Id:" + 1L));

    }


    @Test
    @DisplayName("DELETE /customer/deleteAllCustomers - Should Return 200-OK")
    void deleteAllCustomersShouldReturnOk() throws Exception {
        when(customerService.deleteAllCustomers()).thenReturn(true);


        mockMvc.perform(delete("/customer/deleteAllCustomers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customers has been deleted."));

    }

    @Test
    @DisplayName("DELETE /customer/deleteAllCustomers - Should Return 404-Not Found")
    void deleteAllCustomersShouldReturnNotFound() throws Exception {
        when(customerService.deleteAllCustomers()).thenReturn(false);


        mockMvc.perform(delete("/customer/deleteAllCustomers"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found."));

    }

    @Test
    @DisplayName("DELETE /customer/deleteAllCustomers - Should Return 500-Internal Server Error")
    void deleteAllCustomersShouldReturnInternalServerError() throws Exception {
        when(customerService.deleteAllCustomers()).thenThrow(new NotDeletedException("An error occurred while deleting customers: "));

        mockMvc.perform(delete("/customer/deleteAllCustomers"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while deleting customers: "));

    }

    @Test
    @DisplayName("DELETE /customer/deleteCustomersById/1 - Should Return 200-OK")
    void deleteCustomerByIdShouldReturnOk() throws Exception {

        when(customerService.deleteCustomerById(1L)).thenReturn(true);

        mockMvc.perform(delete("/customer/deleteCustomerById/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer has been deleted."));
    }

    @Test
    @DisplayName("DELETE /customer/deleteCustomersById/1 - Should Return 404-Not Found")
    void deleteCustomerByIdShouldReturnNotFound() throws Exception {

        when(customerService.deleteCustomerById(1L)).thenThrow(new CustomerNotFoundException("Customer not found."));

        mockMvc.perform(delete("/customer/deleteCustomerById/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found."));
    }

    @Test
    @DisplayName("DELETE /customer/deleteCustomersById/1 - Should Return 500 -Internal Server Error")
    void deleteCustomerByIdShouldReturnInternalServerError() throws Exception {

        when(customerService.deleteCustomerById(1L)).thenThrow(new NotDeletedException("An error occurred while deleting the customer: "));

        mockMvc.perform(delete("/customer/deleteCustomerById/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while deleting the customer: "));
    }



}