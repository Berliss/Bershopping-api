package com.bersoft.bershopping.services;



import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.product.Product;

import java.util.List;

public interface IProductService {

    List<Product> findAll();

    Product findById(Long id);

    List<Product> updateProductStock(List<OrderItem> orderItems);
}
