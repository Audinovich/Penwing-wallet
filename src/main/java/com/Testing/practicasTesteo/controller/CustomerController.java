package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("Customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(customers, HttpStatus.OK);
        }
    }
    @GetMapping("/getCustomerById/{id}")
    public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable("customerId") long customerId) {
        Optional<Customer> customer = customerService.getCustomerById(customerId);
        return customer.map(value -> new ResponseEntity<>(Optional.of(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(Optional.empty(), HttpStatus.NOT_FOUND));
    }

    @PostMapping("/saveCustomer")
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<Optional<Customer>> updateCustomer(@RequestBody Customer customer, @PathVariable("id") long id) {
        try {
            Optional<Customer> updatedCustomer = customerService.updateCustomerById(customer, id);
            return updatedCustomer.map(value -> new ResponseEntity<>(Optional.of(value), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAllCustomers")
    public ResponseEntity<String> deleteAllCustomers() {
        try {
            boolean customerFound = customerService.deleteAllCustomers();
            if (customerFound) {
                return new ResponseEntity<>("Todos los clientes han sido eliminados.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No se encontraron clientes para eliminar.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCustomerById/{id}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable("id") long id) {
        try {
            if (customerService.deleteCustomerById(id)) {
                return new ResponseEntity<>("El cliente ha sido eliminado.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cliente no encontrado.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
