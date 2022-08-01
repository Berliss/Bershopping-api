package com.bersoft.bershopping.services;

import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.persistence.repositories.ICustomerRepository;
import com.bersoft.bershopping.persistence.repositories.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Basket basket;

    private Customer customer;

    private Product product;

    private Order order;

    @BeforeEach
    void setUp() {

        //populate a basket
        Address address = new Address();
        address.setId(1L);
        address.setDescription("new york");

        PaymentMethod pm = new PaymentMethod();
        pm.setId(1L);
        pm.setBalance(9999d);
        pm.setNumberReference("3456789");
        pm.setPaymentType("credit card");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("berlis");
        customer.setLastName("rodriguez");
        customer.setEmail("berlisy2j@hotmail.com");
        customer.setAddresses(Arrays.asList(address));
        customer.setPaymentMethods(Arrays.asList(pm));
        customer.setOrders(null);

        product = new Product(1L, 1000d, "shoes", 10d);

        basket = new Basket();
        basket.setId(1L);
        basket.setCreateAt(new Date());
        basket.setCustomer(customer);
        basket.setDelivery(customer.getAddresses().get(0));
        basket.setPaymentMethod(customer.getPaymentMethods().get(0));

        List<BasketItem> itemList = new ArrayList<>();
        itemList.add(new BasketItem(1d, product.getPrice(), product));

        basket.setBasketItems(itemList);

        order = new Order();
        order.setId(1L);
        order.setCustomer(basket.getCustomer());
        order.setDelivery(basket.getDelivery());
        order.setCreateAt(new Date());

        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(new OrderItem(1d, product.getPrice(), product));

        order.setOrderItemList(orderItemList);
        order.setPaymentMethod(basket.getCustomer().getPaymentMethods().get(0));

        customer.setBasket(basket);
        customer.setOrders(Arrays.asList(order));

    }

    @Nested
    @DisplayName("Tests on 'createOrder' ")
    class TestCreateOrder{

        @Test
        @DisplayName("Test 'createOrder', expected -> return a order with a id.")
        void testCreateOrder() {

            //given
            order.setId(null);
            when(orderRepository.save(order)).thenAnswer(invocation -> {
                order.setId(3L);
                return order;
            });

            //when
            Order createdOrder = orderService.createOrder(order,customer);

            //then
            assertNotNull(createdOrder);
            assertNotNull(createdOrder.getId());
            assertEquals(3L,createdOrder.getId());

        }

        @Test
        @DisplayName("Test 'createOrder' when invalids arguments, expected -> throws IllegalArgumentException.")
        void testCreateBasketWhenInvalidValuesOnArguments() {

            //given
            //when
            //then
            assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null,null));

            verify(orderRepository, never()).save(any());

        }

    }
}