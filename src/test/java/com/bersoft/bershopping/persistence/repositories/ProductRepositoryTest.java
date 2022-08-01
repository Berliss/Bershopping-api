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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    IProductRepository productRepository;

    //findById
    //findAll
    //Save
    @Test
    @DisplayName("Test 'findById', expected -> a present value")
    void testFindById() {

        //given
        //when
        Optional<Product> productFound = productRepository.findById(1L);

        //then
        assertTrue(productFound.isPresent(), "customer is not present");
        assertEquals("iphone 6", productFound.get().getDescription());

    }

    @Test
    @DisplayName("Test 'findAll', expected -> a present value")
    void testFindAll() {

        //given
        //when
        List<Product> productList = (List<Product>) productRepository.findAll();

        //then
        assertNotNull(productList);
        assertFalse(productList.isEmpty());

    }

    @Test
    @DisplayName("Test 'save', expected -> a non null product created with a id assigned")
    void testSave() {

        //given
        Product product = new Product();
        product.setDescription("phone");
        product.setPrice(300d);
        product.setStock(10d);

        //when
        Product productCreated =  productRepository.save(product);

        //then
        assertNotNull(product);
        assertNotNull(product.getId());

    }

}