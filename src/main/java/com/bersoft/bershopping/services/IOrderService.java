package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.checkout.Order;

public interface IOrderService {
    Order createOrder(Order order);
}
