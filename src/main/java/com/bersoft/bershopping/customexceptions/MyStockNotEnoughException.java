package com.bersoft.bershopping.customexceptions;

import com.bersoft.bershopping.persistence.entities.product.Product;

public class MyStockNotEnoughException extends RuntimeException {

    public MyStockNotEnoughException(String message) {
        super(message);
    }

    public MyStockNotEnoughException(Product p, Double qtyToSell) {
        super("stock:" + p.getStock() + " trying to sell " + qtyToSell);
    }
}
