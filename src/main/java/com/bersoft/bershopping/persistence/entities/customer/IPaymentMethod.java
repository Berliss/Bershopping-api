package com.bersoft.bershopping.persistence.entities.customer;

public interface IPaymentMethod {
    Double debit(double amount);
    Double getBalance();
}
