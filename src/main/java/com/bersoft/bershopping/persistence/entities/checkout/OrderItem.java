package com.bersoft.bershopping.persistence.entities.checkout;

import com.bersoft.bershopping.persistence.entities.product.Product;

import javax.persistence.*;

@Entity
@Table(name = "orden_items")
public class OrderItem extends AbstractItem {

    public OrderItem() {
    }

    public OrderItem(Double quantity, Double price, Product product) {
        super(quantity, price, product);
    }

}
