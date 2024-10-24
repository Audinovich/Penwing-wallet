package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Article;
import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.AuthenticationException;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.Testing.practicasTesteo.respository.CreditRepository;
import com.Testing.practicasTesteo.respository.CustomerRepository;
import com.Testing.practicasTesteo.respository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Customer getCustomerById(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer saveCustomer(Customer customer) {
        // Guardar el cliente
        Customer savedCustomer = customerRepository.save(customer);

        // Crear y asociar el wallet
        Wallet wallet = Wallet.builder()
                .name(savedCustomer.getName() + "'s Wallet")
                .customer(savedCustomer)
                .build();

        List<Article> existingArticles = articleRepository.findAll();
        wallet.setArticles(existingArticles);
        walletRepository.save(wallet);
        savedCustomer.setWallet(wallet);

        // Crear y asociar el crédito
        Credit credit = Credit.builder()
                .customer(savedCustomer)
                .euro(0L)
                .bitcoin(0L)
                .ethereum(0L)
                .ripple(0L)
                .litecoin(0L)
                .cardano(0L)
                .build();
        creditRepository.save(credit);

        // Guardar la entidad Customer actualizada con relaciones establecidas
        return customerRepository.save(savedCustomer);
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
            customerRepository.deleteAll();
            return true;

        } catch (Exception e) {
            throw new CustomerNotFoundException("Customers no encontrados.");
        }
    }

    @Override
    public boolean deleteCustomerById(long id) {
        try {
            Optional<Customer> customerFind = customerRepository.findById(id);
            return true;
        } catch (Exception e) {
            throw new CustomerNotFoundException("Customer ID " + id + " no encontrado.");
        }

    }

    @Override
    public Customer authenticate(String email, String password) {
        try {
            Optional<Customer> usuario = customerRepository.findByEmailAndPassword(email, password);
            if (usuario.isPresent()) {
                return usuario.get();
            } else {
                throw new AuthenticationException("Wrong credentials.");
            }
        } catch (Exception e) {
            throw new AuthenticationException("Authentication Failed", e);
        }
    }
}