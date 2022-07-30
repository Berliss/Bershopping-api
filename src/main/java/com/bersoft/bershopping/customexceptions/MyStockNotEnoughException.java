package com.bersoft.bershopping.customexceptions;

import com.bersoft.bershopping.persistence.entities.product.Product;

public class MyStockNotEnoughException extends RuntimeException {

    public MyStockNotEnoughException(String message) {
        super(message);
    }

    public MyStockNotEnoughException(Product p, Double qtyToAdd) {
        super("stock: " + p.getStock() + " trying to add " + qtyToAdd);
    }
}
