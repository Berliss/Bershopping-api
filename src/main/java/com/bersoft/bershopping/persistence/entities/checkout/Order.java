package com.bersoft.bershopping.persistence.entities.checkout;

import com.bersoft.bershopping.persistence.entities.customer.Customer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends AbstractOrder {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public void addItem(OrderItem item) {
        orderItemList.add(item);
    }

    public Double getTotal() {
        Double total = 0.0;
        for (OrderItem l : orderItemList) {
            total += l.calcImport();
        }
        return total;
    }

    //getters & setters
    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
