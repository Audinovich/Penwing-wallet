package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Credit;
import com.Testing.practicasTesteo.entity.Customer;
import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.CustomerNotFoundException;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.respository.ArticleRepository;
import com.Testing.practicasTesteo.respository.CreditRepository;
import com.Testing.practicasTesteo.respository.CustomerRepository;
import com.Testing.practicasTesteo.respository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {


    @Mock
    CustomerRepository customerRepository;

    @Mock
    WalletRepository walletRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    CreditRepository creditRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    private Customer customer;
    private List<Customer> customers;
    private Wallet wallet;
    private Credit credit;


    @BeforeEach
    void setUp() {
        wallet = Wallet.builder()
                .name("Juan's Wallet")
                .customer(null)
                .build();

        credit = Credit.builder()
                .bitcoin(1.0)
                .customer(null)
                .build();

        customer = Customer.builder()
                .customerId(1L)
                .name("juan")
                .surname("Perez")
                .email("juanPerez@gmail.com")
                .wallet(wallet)
                .credits(List.of(credit))
                .build();
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers_WhenCustomersExist() {
        List<Customer> listOfCustomers = List.of(customer);

        when(customerRepository.findAll()).thenReturn(listOfCustomers);

        List<Customer> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("juan",result.get(0).getName());

        verify(customerRepository,times(1)).findAll();

    }
    @Test
    void getAllCustomers_ShouldThrowCustomerNotFoundException_WhenNoCustomersExist() {

        when(customerRepository.findAll()).thenReturn(List.of());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getAllCustomers();
        });

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository,times(1)).findAll();
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {

        //Todo por que optional?
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(customer);
        assertEquals("juan",result.getName());
        assertEquals("juanPerez@gmail.com",result.getEmail());

        verify(customerRepository,times(1)).findById(1L);
    }
    @Test
    void getCustomerById_ShouldThrowException_WhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class , ()-> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer not found with id: " + customer.getCustomerId(),exception.getMessage());
        verify(customerRepository,times(1)).findById(1L);
    }

    @Test
    void saveCustomer_ShouldSaveAndReturnCustomer_WhenValidDataProvided() {

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);

        Customer result = customerService.saveCustomer(customer);

        assertNotNull(result);
        assertEquals("juan",result.getName());
        assertEquals("Juan's Wallet",wallet.getName());
        assertEquals(1.0,credit.getBitcoin());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(creditRepository, times(1)).save(any(Credit.class));
    }

    @Test
    void saveCustomer_ShouldThrowException_WhenCustomerDoesNotSave() {

        when(customerRepository.save(any(Customer.class))).thenThrow(NotSavedException.class);


        Exception exception = assertThrows(NotSavedException.class,()->{
            customerService.saveCustomer(customer);
        });

        assertEquals("Failed to save customer data: ",exception.getMessage());
        verify(customerRepository,times(1)).save(customer);

    }

    @Test
    void updateCustomerById() {
    }

    @Test
    void deleteAllCustomers() {
    }

    @Test
    void deleteCustomerById() {
    }

    @Test
    void authenticate() {
    }
}