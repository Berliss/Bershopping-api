package com.bersoft.bershopping.utils.mappers;

import com.bersoft.bershopping.utils.dtos.BasketDto;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.customer.Customer;

import java.text.SimpleDateFormat;
import java.util.*;

public class BasketMapper {

    public BasketDto mapBasketToBasketDto(Basket b) {

        Customer c = b.getCustomer();
        Map<String,Object> customer = new LinkedHashMap<>();

        customer.put("id", c.getId());
        customer.put("firstName", c.getFirstName());
        customer.put("lastName", c.getLastName());
        customer.put("email", c.getEmail());

        return new BasketDto(b.getId(),
                new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(b.getCreateAt()),
                b.getBasketItems().size(),
                b.getTotal(),
                b.getPaymentMethod(),
                b.getDelivery(),
                customer,
                new ItemMapper().mapItemsToItemsDto(b.getBasketItems())
        );
    }

}
