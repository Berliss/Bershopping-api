package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemValidatorTest {

    @Test
    @DisplayName("Test when at least a item exist, expected -> true")
    void testWhenAtLeastAItemExist() {

        //given
        List<BasketItem> itemList = new ArrayList<>();
        itemList.add(new BasketItem());

        //when
        ItemValidator itemValidator = new ItemValidator(itemList);

        //then
        assertTrue(itemValidator.validate());

    }

    @Test
    @DisplayName("Test when item list is empty, expected -> false")
    void testWhenItemsIsEmpty() {

        //given
        List<BasketItem> itemList = new ArrayList<>();

        //when
        ItemValidator itemValidator = new ItemValidator(itemList);

        //then
        assertFalse(itemValidator.validate());

    }

}