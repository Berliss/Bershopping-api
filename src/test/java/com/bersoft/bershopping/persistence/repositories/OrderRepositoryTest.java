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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    IOrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        //populate
        Customer customer = new Customer();
        customer.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
    }

    @Test
    @DisplayName("Test 'create', expected -> a non null order created with a id assigned")
    void testCreate() {

        //given
        order.setId(null);

        //when
        Order orderCreated = orderRepository.save(order);

        //then
        assertNotNull(orderCreated);
        assertNotNull(orderCreated.getId());

    }

}