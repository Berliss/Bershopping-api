package com.bersoft.bershopping.persistence.entities.checkout;

import com.bersoft.bershopping.persistence.entities.product.Product;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double quantity ;

    @Column(nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Product product;

    protected AbstractItem() {
    }

    protected AbstractItem(Double quantity, Double price, Product product) {
        this.quantity = quantity;
        this.price = price;
        this.product = product;
    }

    public Double calcImport() {
        return quantity * product.getPrice();
    }

    //getters & setters
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {

        this.product = product;
    }
}
