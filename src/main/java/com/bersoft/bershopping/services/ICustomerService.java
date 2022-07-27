package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.Basket;
import com.bersoft.bershopping.persistence.entities.Customer;
import com.bersoft.bershopping.persistence.entities.Product;

import java.util.Optional;

public interface ICustomerService {

    Customer findCustomerByEmail(String email);

    Basket findCustomerBasket(String email);

    Basket createBasket(Basket basket, Customer customer);

    Basket updateBasket(Basket basket);

    Basket removeProductFromBasket(Basket basket, Product product);

    Basket addProductToBasket(Basket basket, Product product, Double quantity);

    Basket setAddressToBasket(Basket basket, Customer customer, Long addressId);

    Basket setPaymentMethodToBasket(Basket basket, Customer customer, Long paymentMethodId);

    void deleteBasket(Basket basket);
}
