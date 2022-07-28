package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.OrderItem;
import com.bersoft.bershopping.persistence.entities.Product;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    Product findById(Long id);

    List<Product> updateProductStock(List<OrderItem> orderItems);
}
