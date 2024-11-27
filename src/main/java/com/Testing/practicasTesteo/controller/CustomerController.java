package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers()throws CustomerNotFoundException {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/getCustomerById/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") long customerId) {

        try {
            Customer customerFoundById = customerService.getCustomerById(customerId);
            return new ResponseEntity<>(customerFoundById, HttpStatus.OK);
        } catch (CustomerNotFoundException ee) {
            return new ResponseEntity<>(ee.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @PostMapping("/saveCustomer")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (NotSavedException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateCustomer/{customerId}")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer, @PathVariable("customerId") long id) {
        try {
            Customer updatedCustomer = customerService.updateCustomerById(customer, id);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllCustomers")
    public ResponseEntity<?> deleteAllCustomers() {
        try {
            boolean customerFound = customerService.deleteAllCustomers();
            if (customerFound) {
                return new ResponseEntity<>("Customers has been deleted.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Customer not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCustomerById/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable("id") long id) {
        try {
            customerService.deleteCustomerById(id); // No necesitas verificar el booleano.
            return new ResponseEntity<>("Customer has been deleted.", HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
