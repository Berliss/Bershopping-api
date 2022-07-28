package com.bersoft.bershopping.services;

import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.persistence.repositories.IProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    private IProductRepository productRepository;

    public ProductServiceImpl(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> {
            throw new MyResourceNotFoundException("product with the given id '" + id + "' not found");
        });
    }

    @Override
    @Transactional
    public List<Product> updateProductStock(List<OrderItem> orderItems) {
        List<Product> products = new ArrayList<>();
        orderItems.forEach(item -> {
            Product p = item.getProduct();
            double qty = item.getQuantity();
            p.setStock(p.getStock() - qty);
            products.add(productRepository.save(p));
        });
        return products;
    }

}
