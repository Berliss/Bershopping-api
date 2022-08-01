package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerValidatorTest {

    @Test
    @DisplayName("Test when customer exist, expected -> true")
    void testWhenCustomerExist() {

        //given
        //when
        CustomerValidator customerValidator = new CustomerValidator(new Customer());

        //then
        assertTrue(customerValidator.validate());

    }

    @Test
    @DisplayName("Test when customer does not exist, expected -> false")
    void testWhenCustomerDoesNotExist() {

        //given
        //when
        CustomerValidator customerValidator = new CustomerValidator(null);

        //then
        assertFalse(customerValidator.validate());

    }

}