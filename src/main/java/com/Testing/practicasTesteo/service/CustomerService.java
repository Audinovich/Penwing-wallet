package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer> getAllCustomers();
    Customer getCustomerById(long id);
    Customer saveCustomer(Customer c);
    Customer updateCustomerById(Customer c, long id);
    boolean deleteAllCustomers();
    boolean deleteCustomerById(long id);
    Customer authenticate(String email, String password);


}
