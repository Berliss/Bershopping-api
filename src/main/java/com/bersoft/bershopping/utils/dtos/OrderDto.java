package com.bersoft.bershopping.utils.dtos;

import com.bersoft.bershopping.persistence.entities.customer.Address;

import java.util.List;
import java.util.Map;

public record OrderDto(long orderId,
                       String createAt,
                       int itemsCount,
                       double totalAmount,
                       PaymentMethodDto paymentMethod,
                       Address address,
                       Map<String, Object> customer,
                       List<ItemDto> items) {
}
