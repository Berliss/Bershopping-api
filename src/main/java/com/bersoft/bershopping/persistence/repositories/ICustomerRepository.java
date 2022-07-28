package com.bersoft.bershopping.persistence.repositories;

import com.bersoft.bershopping.persistence.entities.customer.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ICustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

}
