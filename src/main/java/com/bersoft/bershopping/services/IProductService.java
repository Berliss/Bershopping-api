package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    List<Product> findAll();

    Product findById(Long id);
}
