package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.customer.Customer;

public interface IOrderService {
    Order createOrder(Order order, Customer customer);
}
