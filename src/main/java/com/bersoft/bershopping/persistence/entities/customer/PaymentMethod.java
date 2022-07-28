package com.bersoft.bershopping.persistence.entities.customer;

import javax.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod implements IPaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "number_reference", nullable = false)
    private String numberReference;

    @Column(nullable = false)
    private Double balance;

    @Override
    public Double debit(double amount){
        this.balance -= amount;
        return this.balance;
    }

    @Override
    public Double getBalance(){
        return balance;
    }

    //getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getNumberReference() {
        return numberReference;
    }

    public void setNumberReference(String numberReference) {
        this.numberReference = numberReference;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
