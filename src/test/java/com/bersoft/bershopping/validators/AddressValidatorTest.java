package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressValidatorTest {

    @Test
    @DisplayName("Test when address exist, expected -> true")
    void testWhenAddressExist() {

        //given
        //when
        AddressValidator addressValidator = new AddressValidator(new Address());

        //then
        assertTrue(addressValidator.validate());

    }

    @Test
    @DisplayName("Test when Address does not exist, expected -> false")
    void testWhenAddressDoesNotExist() {

        //given
        //when
        AddressValidator addressValidator = new AddressValidator(null);

        //then
        assertFalse(addressValidator.validate());

    }

}