package com.bersoft.bershopping.services;

import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.persistence.repositories.IBasketRepository;
import com.bersoft.bershopping.persistence.repositories.ICustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class CustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Basket basket;

    private Customer customer;

    private Product product;

    @BeforeEach
    void setUp() {

        //populate a basket
        Address address = new Address();
        address.setId(1L);
        address.setDescription("new york");

        PaymentMethod pm = new PaymentMethod();
        pm.setId(1L);
        pm.setBalance(9999d);
        pm.setNumberReference("3456789");
        pm.setPaymentType("credit card");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("berlis");
        customer.setLastName("rodriguez");
        customer.setEmail("berlisy2j@hotmail.com");
        customer.setAddresses(Arrays.asList(address));
        customer.setPaymentMethods(Arrays.asList(pm));
        customer.setOrders(null);

        product = new Product(1L, 1000d, "shoes", 10d);

        basket = new Basket();
        basket.setId(1L);
        basket.setCreateAt(new Date());
        basket.setCustomer(customer);
        basket.setDelivery(customer.getAddresses().get(0));
        basket.setPaymentMethod(customer.getPaymentMethods().get(0));

        List<BasketItem> itemList = new ArrayList<>();
        itemList.add(new BasketItem(1d, product.getPrice(), product));

        basket.setBasketItems(itemList);

        customer.setBasket(basket);


    }

    @Nested
    @DisplayName("Tests on 'findCustomerByEmail' ")
    class TestsFindCustomerBasket {

        @Test
        @DisplayName("Test 'findCustomerByEmail', expected -> return not null customer.")
        void testFindCustomerBasket() {

            //given
            when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

            //when
            Customer customerFound = customerService.findCustomerByEmail(anyString());

            //then
            assertNotNull(customerFound);

        }

        @Test
        @DisplayName("Test 'findCustomerByEmail' when customer does not exist, expected -> throws MyResourceNotFoundException.")
        void testFindCustomerBasketWhenCustomerDoesNotExist() {

            //given
            when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //when
            // then
            assertThrows(MyResourceNotFoundException.class, () -> customerService.findCustomerByEmail(anyString()));

        }

    }

}