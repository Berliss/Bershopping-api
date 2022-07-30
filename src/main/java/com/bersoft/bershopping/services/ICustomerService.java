package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.customer.Customer;

public interface ICustomerService {

    Customer findCustomerByEmail(String email);
}
