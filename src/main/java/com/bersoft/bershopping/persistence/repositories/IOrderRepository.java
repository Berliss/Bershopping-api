package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.checkout.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IOrderRepository extends PagingAndSortingRepository<Order,Long> {
}
