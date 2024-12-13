package com.Testing.practicasTesteo.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

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
        assertEquals(1, result.size());
        assertEquals("juan", result.get(0).getName());

        verify(customerRepository, times(1)).findAll();

    }

    @Test
    void getAllCustomers_ShouldThrowCustomerNotFoundException_WhenNoCustomersExist() {

        when(customerRepository.findAll()).thenReturn(List.of());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getAllCustomers();
        });

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {

        //Todo por que optional?
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(customer);
        assertEquals("juan", result.getName());
        assertEquals("juanPerez@gmail.com", result.getEmail());

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_ShouldThrowException_WhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer not found with id: " + customer.getCustomerId(), exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void saveCustomer_ShouldSaveAndReturnCustomer_WhenValidDataProvided() {

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);

        Customer result = customerService.saveCustomer(customer);

        assertNotNull(result);
        assertEquals("juan", result.getName());
        assertEquals("Juan's Wallet", wallet.getName());
        assertEquals(1.0, credit.getBitcoin());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(creditRepository, times(1)).save(any(Credit.class));
    }

    @Test
    void saveCustomer_ShouldThrowException_WhenCustomerDoesNotSave() {

        when(customerRepository.save(any(Customer.class))).thenThrow(NotSavedException.class);


        Exception exception = assertThrows(NotSavedException.class, () -> {
            customerService.saveCustomer(customer);
        });

        assertEquals("Failed to save customer data: ", exception.getMessage());
        verify(customerRepository, times(1)).save(customer);

    }

    @Test
    void updateCustomerById_validId_shouldUpdateCustomer() {


        Customer customerFound = customer;
        customerFound.setName("Old Name");

        Customer updatedCustomerData = Customer.builder()
                .name("New Name")
                .surname("New Surname")
                .password("NewPassword")
                .address("New Address")
                .phone("123456789")
                .email("newemail@example.com")
                .build();


        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerFound));
        // El invocation devuelve el (0) primer argumento , es decir recibe el updatedCustomerData y lo devuelve como haria el repo
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Customer result = customerService.updateCustomerById(updatedCustomerData, 1L);

        assertNotNull(result, "The result should not be null");
        assertEquals(updatedCustomerData.getName(), result.getName(), "The name should be updated");
        assertEquals(updatedCustomerData.getEmail(), result.getEmail(), "The email should be updated");

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customerFound);
    }

    @Test
    void updateCustomerById_invalidId_shouldThrowCustomerNotFoundException() {

        Customer customerFound = customer;
        customerFound.setName("Old Name");

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,()->{
           customerService.updateCustomerById(customerFound,1L);
        }, "Expected CustomerNotFoundException to be thrown");

        verify(customerRepository,times(1)).findById(1L);
        verify(customerRepository,never()).save (any(Customer.class));

    }

    @Test
    void deleteAllCustomers_ShouldDeleteAllCustomers_WhenCustomersExist() {
        when(customerRepository.count()).thenReturn(5L);

        boolean result = customerService.deleteAllCustomers();

        assertTrue(result);
        verify(customerRepository, times(1)).deleteAll();

    }

    @Test
    void deleteAllCustomers_ShouldReturnFalse_WhenNoCustomersExist() {
        when(customerRepository.count()).thenReturn(0L);

        boolean result = customerService.deleteAllCustomers();

        assertFalse(result);
        verify(customerRepository, never()).deleteAll();

    }

    @Test
    void deleteAllCustomers_ShouldThrowNotDeletedException_WhenRepositoryThrowsException() {
        // Arrange
        when(customerRepository.count()).thenThrow(new DataAccessException("Database error") {
        });

        // Act & Assert
        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            customerService.deleteAllCustomers();
        });

        assertEquals("An error occurred while deleting customers: Database error", exception.getMessage());
        verify(customerRepository).count();
    }

    @Test
    void deleteAllCustomers_ShouldThrowNotDeletedException_WhenDeleteFails() {
        when(customerRepository.count()).thenReturn(5L);
        doThrow(new DataAccessException("Database Error") {
        }).when(customerRepository).deleteAll();

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            customerService.deleteAllCustomers();
        });
        assertEquals("An error occurred while deleting customers: Database Error", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);

        verify(customerRepository, times(1)).deleteAll();
        verify(customerRepository, times(1)).count();
    }

    @Test
    void deleteCustomerById_ShouldDeleteCustomer_WhenCustomerIdExist() {
        when(customerRepository.count()).thenReturn(3L);

        boolean result = customerService.deleteCustomerById(3L);

        assertTrue(result);
        verify(customerRepository, times(1)).deleteById(3L);

    }

    @Test
    void deleteCustomerById_ShouldReturnFalse_WhenNoCustomersExist() {
        when(customerRepository.count()).thenReturn(0L);

        boolean result = customerService.deleteCustomerById(1L);

        assertFalse(result);
        verify(customerRepository, never()).deleteById(anyLong());

    }

    @Test
    void deleteCustomerById_ShouldThrowNotDeletedException_WhenRepositoryThrowsException() {
        when(customerRepository.count()).thenThrow(new DataAccessException("Database Error") {
        });

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            customerService.deleteCustomerById(1L);
        });

        assertEquals("An error occurred while deleting customers: Database Error", exception.getMessage());
        verify(customerRepository).count();
    }

    @Test
    void deleteCustomerById_ShouldThrowNotDeletedException_WhenDeleteFails() {
        when(customerRepository.count()).thenReturn(4L);
        doThrow(new DataAccessException("Database Error") {
        }).when(customerRepository).deleteById(4L);

        NotDeletedException exception = assertThrows(NotDeletedException.class, () -> {
            customerService.deleteCustomerById(4L);
        });
        assertEquals("An error occurred while deleting customers: Database Error", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);

        verify(customerRepository, times(1)).count();
        verify(customerRepository, times(1)).deleteById(4L);
    }


    @Test
    void authenticate_validCredentials_shouldReturnCustomer(){

        when(customerRepository.findByEmailAndPassword("juanPerez@gmail.com", String.valueOf(1234)))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.authenticate("juanPerez@gmail.com", String.valueOf(1234));

        assertNotNull(result);
        verify(customerRepository,times(1))
                .findByEmailAndPassword("juanPerez@gmail.com",String.valueOf(1234));
    }

    @Test
    void authenticate_invalidCredentials_shouldReturnAuthenticationException(){

        when(customerRepository.findByEmailAndPassword("juanPerez@gmail.com", String.valueOf(1234)))
                .thenReturn(Optional.empty());

        AuthenticationException exception = assertThrows(AuthenticationException.class,()->{
            customerService.authenticate("juanPerez@gmail.com", String.valueOf(1234));
        },"Expected AuthenticationException to be thrown");


        verify(customerRepository,times(1))
                .findByEmailAndPassword("juanPerez@gmail.com",String.valueOf(1234));
    }

}