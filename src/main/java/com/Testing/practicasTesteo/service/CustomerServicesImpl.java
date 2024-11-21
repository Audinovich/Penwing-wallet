package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.AuthenticationException;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotDeletedException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.Testing.practicasTesteo.respository.CreditRepository;
import com.Testing.practicasTesteo.respository.CustomerRepository;
import com.Testing.practicasTesteo.respository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerServicesImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CreditRepository creditRepository;


    @Override
    public List<Customer> getAllCustomers() {

        try {
            List<Customer> customerFound = customerRepository.findAll();
            if (customerFound.isEmpty()) {
                throw new CustomerNotFoundException("Customer not found");
            }
            return customerFound;
        } catch (Exception e) {
            throw new RuntimeException("Fail to get clients", e);
        }
    }

    @Override
    public Customer getCustomerById(long customerId) throws CustomerNotFoundException {

        try {
            return customerRepository.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage(), e);

        }
    }

    //TODO REVISAR ESTO
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer saveCustomer(Customer customer) {
        try {
            // Guardar el cliente
            Customer savedCustomer = customerRepository.save(customer);

            // Crear la billetera
            Wallet wallet = Wallet.builder()
                    .name(savedCustomer.getName() + "'s Wallet")
                    .customer(savedCustomer)
                    .build();

            // Asignar artículos existentes
            List<Article> existingArticles = articleRepository.findAll();
            wallet.setArticles(existingArticles);

            walletRepository.save(wallet);
            savedCustomer.setWallet(wallet);

            // Crear crédito asociado
            Credit credit = Credit.builder()
                    .customer(savedCustomer)
                    .euro(0.0)
                    .bitcoin(0.0)
                    .ethereum(0.0)
                    .ripple(0.0)
                    .litecoin(0.0)
                    .cardano(0.0)
                    .build();

            creditRepository.save(credit);

            // Guardar cliente con billetera asociada
            return customerRepository.save(savedCustomer);

        } catch (NotSavedException e) {

            throw new NotSavedException("Failed to save customer data: ");
        } catch (Exception e) {

            throw new RuntimeException("Unexpected error while saving customer: ");
        }
    }

    @Override
    public Customer updateCustomerById(Customer c, long id) {
        Optional<Customer> customerFound = customerRepository.findById(id);
        if (customerFound.isPresent()) {
            Customer customerUpdated = customerFound.get();
            customerUpdated.setName(c.getName());
            customerUpdated.setSurname(c.getSurname());
            customerUpdated.setPassword(c.getPassword());
            customerUpdated.setAddress(c.getAddress());
            customerUpdated.setPhone(c.getPhone());
            customerUpdated.setEmail(c.getEmail());

            return customerRepository.save(customerUpdated);
        } else {

            throw new CustomerNotFoundException("Customer ID " + id + "no encontrado.");
        }

    }


    @Override
    public boolean deleteAllCustomers() {
        try {

            long count = customerRepository.count();
            if (count == 0) {
                return false;
            }
            customerRepository.deleteAll();
            return true;
        } catch (Exception e) {
            throw new NotDeletedException("An error occurred while deleting customers: " + e.getMessage(), e);
        }

    }


    @Override
    public boolean deleteCustomerById(long id) {
        try {
            Optional<Customer> customerFind = customerRepository.findById(id);
            if (customerFind.isPresent()) {
                customerRepository.deleteById(id);
                return true;
            } else {
                throw new CustomerNotFoundException("Customer not found.");
            }
        } catch (Exception e) {
            throw new NotDeletedException("An error occurred while deleting the customer.");
        }

    }

    @Override
    public Customer authenticate(String email, String password) {
        try {
            Optional<Customer> user = customerRepository.findByEmailAndPassword(email, password);
            if (user.isPresent()) {
                return user.get();
            } else {
                throw new AuthenticationException("Wrong credentials.");
            }
        } catch (Exception e) {
            throw new AuthenticationException("Authentication Failed", e);
        }
    }
}