package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentValidatorTest {

    @Test
    @DisplayName("Test when balance is 0 & amount is 0', expected -> true")
    void testPay() {

        //given
        PaymentMethod pay = new PaymentMethod();
        pay.setBalance(0d);

        //when
        PaymentValidator paymentValidator = new PaymentValidator(pay, 0);

        //then
        assertTrue(paymentValidator.validate());

    }

    @Test
    @DisplayName("Test when balance is lower than amount', expected -> false")
    void testPayWhenBalanceLower() {

        //given
        PaymentMethod pay = new PaymentMethod();
        pay.setBalance(0d);

        //when
        PaymentValidator paymentValidator = new PaymentValidator(pay, 100);

        //then
        assertFalse(paymentValidator.validate());

    }

    @Test
    @DisplayName("Test when balance is greater than amount', expected -> false")
    void testPayWhenBalanceGreater() {

        //given
        PaymentMethod pay = new PaymentMethod();
        pay.setBalance(200d);

        //when
        PaymentValidator paymentValidator = new PaymentValidator(pay, 100);

        //then
        assertTrue(paymentValidator.validate());

    }
}