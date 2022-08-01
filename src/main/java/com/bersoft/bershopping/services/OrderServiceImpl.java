package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.repositories.IOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements IOrderService {

    private IOrderRepository orderRepository;

    public OrderServiceImpl(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    @Transactional
    public Order createOrder(Order order, Customer customer) {
        if (order != null && order.getId() == null && customer!=null && customer.getId() != null) {
            order.setCustomer(customer);
            return orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("order, customer, customer id & order id can not be null");
        }
    }

}
