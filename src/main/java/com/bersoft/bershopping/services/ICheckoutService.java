package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.product.Product;

public interface ICheckoutService {

    Basket findCustomerBasket(String email);

    Basket createBasket(Basket basket, Customer customer);

    Basket updateBasket(Basket basket);

    Basket removeProductFromBasket(Basket basket, Product product);

    Basket addProductToBasket(Basket basket, Product product, Double quantity);

    Basket setAddressToBasket(Basket basket, Long addressId);

    Basket setPaymentMethodToBasket(Basket basket, Long paymentMethodId);

    void deleteBasket(Basket basket);
}
