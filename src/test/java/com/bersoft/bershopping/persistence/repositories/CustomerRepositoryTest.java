package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    ICustomerRepository customerRepository;

    @Test
    @DisplayName("Test 'findByEmail', expected -> a present value")
    void testFindByEmail() {

        //given
        //when
        Optional<Customer> customerFound = customerRepository.findByEmail("berlisy2j@hotmail.com");

        //then
        assertTrue(customerFound.isPresent(), "customer is not present");
        assertEquals("berlis", customerFound.get().getFirstName());

    }



}