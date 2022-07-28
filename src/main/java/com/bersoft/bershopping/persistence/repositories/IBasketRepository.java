package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IBasketRepository extends PagingAndSortingRepository<Basket,Long> {
}
