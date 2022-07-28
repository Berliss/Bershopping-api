package com.bersoft.bershopping.utils.mappers;

import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.utils.dtos.OrderDto;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderMapper {
    public OrderDto mapOrderToOrderDto(Order o) {

        Customer c = o.getCustomer();
        Map<String,Object> customer = new LinkedHashMap<>();

        customer.put("id", c.getId());
        customer.put("firstName", c.getFirstName());
        customer.put("lastName", c.getLastName());
        customer.put("email", c.getEmail());

        return new OrderDto(o.getId(),
                new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(o.getCreateAt()),
                o.getOrderItemList().size(),
                o.getTotal(),
                o.getPaymentMethod(),
                o.getDelivery(),
                customer,
                new ItemMapper().mapItemsToItemsDto(o.getOrderItemList())
        );
    }
}
