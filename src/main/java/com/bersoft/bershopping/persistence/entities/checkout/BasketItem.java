package com.bersoft.bershopping.persistence.entities.checkout;

import com.bersoft.bershopping.persistence.entities.product.Product;

import javax.persistence.*;

@Entity
@Table(name = "basket_items")
public class BasketItem extends AbstractItem {

    public BasketItem() {
    }

    public BasketItem(Double quantity, Double price, Product product) {
        super(quantity, price, product);
    }

}
