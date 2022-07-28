package com.bersoft.bershopping.persistence.entities.checkout;

import com.bersoft.bershopping.persistence.entities.customer.Customer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "baskets")
public class Basket extends AbstractOrder {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "basket_id")
    private List<BasketItem> basketItems = new ArrayList<>();

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public void addItem(BasketItem item) {
        basketItems.add(item);
    }

    public Double getTotal() {
        Double total = 0.0;
        for (BasketItem l : basketItems) {
            total += l.calcImport();
        }
        return total;
    }

    //getters & setters
    public List<BasketItem> getBasketItems() {
        return basketItems;
    }

    public void setBasketItems(List<BasketItem> basketItems) {
        this.basketItems = basketItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
