package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.Basket;
import com.bersoft.bershopping.persistence.entities.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface IBasketRepository extends PagingAndSortingRepository<Basket,Long> {
}
