package com.bersoft.bershopping.persistence.entities;

import javax.persistence.*;

@Entity
@Table(name = "basket_item")
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public BasketItem() {
    }

    public BasketItem(Long id, Double quantity, Product product) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double calcImport() {
        return quantity.doubleValue() * product.getPrice();
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "id=" + id +
                ", cantidad=" + quantity +
                ", product=" + product +
                '}';
    }
}
