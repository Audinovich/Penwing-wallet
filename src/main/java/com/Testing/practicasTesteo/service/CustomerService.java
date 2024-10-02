package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService  {

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(long id);

    Customer saveCustomer(Customer c);

    Optional<Customer> updateCustomerById(Customer c, long id);


    boolean deleteAllCustomers();

    boolean deleteCustomerById(long id);


}
