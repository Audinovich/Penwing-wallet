package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.respository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



@Service
public class CustomerServicesImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer saveCustomer(Customer c) {
        return customerRepository.save(c);
    }

    @Override
    public Optional<Customer> updateCustomerById(Customer c, long id) {
        Optional<Customer> customerFound = customerRepository.findById(id);
        if (customerFound.isPresent()) {
            Customer customerUpdated = customerFound.get();
            customerUpdated.setName(c.getName());
            customerUpdated.setSurname(c.getSurname());
            customerUpdated.setAddress(c.getAddress());
            customerUpdated.setPhone(c.getPhone());
            customerUpdated.setEmail(c.getEmail());

            return Optional.of(customerRepository.save(customerUpdated));
        } else {

            throw new CustomerNotFoundException("Customer ID " + id + "no encontrado.");
        }

    }


    @Override
    public boolean deleteAllCustomers() {
        try {
            customerRepository.deleteAll();
            return true;

        } catch (Exception e) {
                throw new CustomerNotFoundException("Customers no encontrados.");
        }
    }

    @Override
    public boolean deleteCustomerById(long id) {
        try {
            Optional<Customer> customerFind = getCustomerById(id);
            customerRepository.delete(customerFind.get());
            return true;
        } catch (Exception e) {
            throw new CustomerNotFoundException("Customer ID " + id + " no encontrado.");
        }

    }
}