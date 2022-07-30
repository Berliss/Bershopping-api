package com.bersoft.bershopping.utils.dtos;

import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;

import java.util.List;
import java.util.Map;

public record BasketDto(long checkoutId,
                        String createAt,
                        int itemsCount,
                        double totalAmount,
                        PaymentMethod paymentMethod,
                        Address address,
                        Map<String, Object> customer,
                        List<ItemDto> items) {
}
