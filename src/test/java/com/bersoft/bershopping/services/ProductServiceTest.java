package com.bersoft.bershopping.services;

import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.persistence.repositories.IOrderRepository;
import com.bersoft.bershopping.persistence.repositories.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private List<Product> productList;

    private Order order;

    @BeforeEach
    void setUp() {

        //populate

        productList = new ArrayList<>();
        productList.add(new Product(1L, 1000d, "shoes", 10d));
        productList.add(new Product(2L, 2000d, "jeans", 10d));

        order = new Order();
        order.setId(1L);

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItem(1d, productList.get(0).getPrice(), productList.get(0)));
        orderItemList.add(new OrderItem(1d, productList.get(1).getPrice(), productList.get(1)));

        order.setOrderItemList(orderItemList);

    }

    @Nested
    @DisplayName("Tests on 'findAll' ")
    class TestFindAll {

        @Test
        @DisplayName("Test 'findAll', expected -> a not empty list of products.")
        void testFindAll() {

            //given
            when(productRepository.findAll()).thenReturn(productList);

            //when
            List<Product> currentProductList = productService.findAll();

            //then
            assertFalse(currentProductList.isEmpty());

        }

    }

    @Nested
    @DisplayName("Tests on 'findById' ")
    class TestFindById {

        @Test
        @DisplayName("Test 'findById', expected -> a product found.")
        void testFindById() {

            //given
            when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(productList.get(0)));

            //when
            Product productFound = productService.findById(anyLong());


            //then
            assertNotNull(productFound);
            assertEquals( "shoes",productFound.getDescription());

        }

        @Test
        @DisplayName("Test 'findById' when product not found, expected -> throws MyResourceNotFoundException")
        void testFindByIdWhenProductNotFound() {

            //given
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> productService.findById(anyLong()));

        }

    }

    @Nested
    @DisplayName("Tests on 'updateProductStock' ")
    class TestUpdateProductStock {

        @Test
        @DisplayName("Test 'updateProductStock', expected -> product list with stock modified.")
        void testUpdateProductStock() {

            //given
            Product p1 = order.getOrderItemList().get(0).getProduct();
            Product p2 = order.getOrderItemList().get(1).getProduct();
            p2.setStock(6d);
            when(productRepository.save(any())).thenReturn(p1, p2);

            //when
            List<Product> productListUpdated = productService.updateProductStock(order.getOrderItemList());

            //then
            assertTrue(productListUpdated.get(0).getStock() < 10);
            assertTrue(productListUpdated.get(1).getStock() < 10);
            assertEquals( 9,productListUpdated.get(0).getStock());
            assertEquals(5, productListUpdated.get(1).getStock());
            assertEquals(productListUpdated.size(), productList.size());

        }

    }

}