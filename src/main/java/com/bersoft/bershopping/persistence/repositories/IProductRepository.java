package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IProductRepository extends PagingAndSortingRepository<Product,Long> {
}
