package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BasketRepositoryTest {

    @Autowired
    IBasketRepository basketRepository;

    private Basket basket;

    @BeforeEach
    void setUp() {

        //populate a basket

        Customer customer = new Customer();
        customer.setId(1L);

        basket = new Basket();
        basket.setId(1L);
        basket.setCreateAt(new Date());
        basket.setCustomer(customer);
    }

    @Test
    @DisplayName("Test 'create', expected -> a non null basket created with a id assigned")
    void testCreate() {

        //given
        basket.setId(null);

        //when
        Basket basketCreated = basketRepository.save(basket);

        //then
        assertNotNull(basketCreated);
        assertNotNull(basketCreated.getId());

    }

    @Test
    @DisplayName("Test 'create' when a basket with the same customer linked exist, expected -> one to one violation, throws DataIntegrityViolationException ")
    void testCreateWhenViolateOneToOneConstrain() {

        //given
        basket.setId(null);
        basket.getCustomer().setId(2L);

        //when
        //then
        assertThrows(DataIntegrityViolationException.class,() -> basketRepository.save(basket));

    }

    @Test
    @DisplayName("Test 'deleteById', expected -> basket delete successfully ")
    void testDeleteById() {

        //given
        //when
        basketRepository.deleteById(2L);

        //then
        assertTrue(basketRepository.findById(2L).isEmpty());

    }

    @Test
    @DisplayName("Test 'existById', expected -> true ")
    void testExistById() {

        //given
        //when
        boolean isFound = basketRepository.existsById(1L);

        //then
        assertTrue(isFound);

    }

    @Test
    @DisplayName("Test 'existById' when basket does not exist, expected -> false ")
    void testExistByIdWhenNotFound() {

        //given
        //when
        boolean isFound = basketRepository.existsById(40L);

        //then
        assertFalse(isFound);

    }

}