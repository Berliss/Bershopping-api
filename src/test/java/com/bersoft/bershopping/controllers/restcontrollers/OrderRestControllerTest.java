package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.customexceptions.MyBadOrderException;
import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.services.ICheckoutService;
import com.bersoft.bershopping.services.IOrderService;
import com.bersoft.bershopping.services.IProductService;
import com.bersoft.bershopping.utils.ApiErrorResponse;
import com.bersoft.bershopping.utils.dtos.OrderDto;
import com.bersoft.bershopping.utils.mappers.OrderMapper;
import com.bersoft.bershopping.validators.PaymentValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(OrderRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ICheckoutService checkoutService;

    @MockBean
    private IProductService productService;

    @MockBean
    private IOrderService orderService;

    private ObjectMapper objectMapper;

    private Basket basket;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        //populate a basket
        Address address = new Address();
        address.setDescription("new york");

        PaymentMethod pm = new PaymentMethod();
        pm.setId(1L);
        pm.setBalance(9999d);
        pm.setNumberReference("3456789");
        pm.setPaymentType("credit card");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("berlis");
        customer.setLastName("rodriguez");
        customer.setEmail("berlisy2j@hotmail.com");
        customer.setAddresses(Arrays.asList(address));
        customer.setPaymentMethods(Arrays.asList(pm));
        customer.setOrders(null);
        customer.setBasket(null);

        Product product = new Product(1L, 1000d, "shoes", 10d);

        basket = new Basket();
        basket.setId(1L);
        basket.setCreateAt(new Date());
        basket.setCustomer(customer);
        basket.setDelivery(customer.getAddresses().get(0));
        basket.setPaymentMethod(customer.getPaymentMethods().get(0));
        basket.setBasketItems(Arrays.asList(new BasketItem(1d, product.getPrice(), product)));

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/orders' endpoint METHOD:POST")
    class TestCreateOrder {

        @Test
        @DisplayName("Test on 'createOrder', expected -> 201 response status & order created successfully")
        void testCreateOrder() throws Exception {

            //given
            OrderMapper mapper = new OrderMapper();

            Product product = basket.getBasketItems().get(0).getProduct();
            product.setStock(9d);

            Order order = new Order();
            order.setId(1L);
            order.setCustomer(basket.getCustomer());
            order.setDelivery(basket.getDelivery());
            order.setCreateAt(new Date());
            order.setOrderItemList(Arrays.asList(new OrderItem(1d, product.getPrice(), product)));
            order.setPaymentMethod(basket.getCustomer().getPaymentMethods().get(0));

            OrderDto orderDto = mapper.mapOrderToOrderDto(order);

            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(productService.updateProductStock(anyList())).thenReturn(Arrays.asList(product));
            when(orderService.createOrder(any(Order.class), any(Customer.class))).thenReturn(order);
            doNothing().when(checkoutService).deleteBasket(basket);


            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(orderDto)))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService).updateProductStock(anyList());
            verify(checkoutService).deleteBasket(any(Basket.class));
            verify(orderService).createOrder(any(Order.class),any(Customer.class));

        }

        @Test
        @DisplayName("Test on 'createOrder' when a checkout process does not exist, expected -> 404 response status & throws myResourceNotFoundException")
        void testCreateOrderWhenCheckoutProcessDoestNotExist() throws Exception {

            //given
            when(checkoutService.findCustomerBasket(anyString())).thenThrow(new MyResourceNotFoundException("checkout process is not initialized"));

            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andExpect(jsonPath("$.details", is(Arrays.asList("checkout process is not initialized"))))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService, never()).updateProductStock(anyList());
            verify(checkoutService, never()).deleteBasket(any(Basket.class));
            verify(orderService, never()).createOrder(any(Order.class), any(Customer.class));

        }

        @Test
        @DisplayName("Test on 'createOrder' when customer is null, expected -> 400 response status & throws MyBadOrderException")
        void testCreateWhenOrderCustomerIsNull() throws Exception {

            //given
            basket.setCustomer(null);
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);

            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyBadOrderException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService, never()).updateProductStock(anyList());
            verify(checkoutService, never()).deleteBasket(any(Basket.class));
            verify(orderService, never()).createOrder(any(Order.class), any(Customer.class));

        }

        @Test
        @DisplayName("Test on 'createOrder' when delivery is null, expected -> 400 response status & throws MyBadOrderException")
        void testCreateWhenOrderDeliveryIsNull() throws Exception {

            //given
            basket.setDelivery(null);
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);

            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyBadOrderException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService, never()).updateProductStock(anyList());
            verify(checkoutService, never()).deleteBasket(any(Basket.class));
            verify(orderService, never()).createOrder(any(Order.class), any(Customer.class));

        }

        @Test
        @DisplayName("Test on 'createOrder' when payment method balance is not enough, expected -> 400 response status & throws MyBadOrderException")
        void testCreateOrderPaymentBalanceNotEnough() throws Exception {

            //given
            basket.getPaymentMethod().setBalance(1d);
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);

            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyBadOrderException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService, never()).updateProductStock(anyList());
            verify(checkoutService, never()).deleteBasket(any(Basket.class));
            verify(orderService, never()).createOrder(any(Order.class), any(Customer.class));

        }

        @Test
        @DisplayName("Test on 'createOrder' when payment method balance is not enough, expected -> 400 response status & throws MyBadOrderException")
        void testCreateOrderWhenNotItemsAdded() throws Exception {

            //given
            basket.setBasketItems(new ArrayList<>());
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);

            //when
            mvc.perform(post("/api/v1/bershopping/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyBadOrderException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(productService, never()).updateProductStock(anyList());
            verify(checkoutService, never()).deleteBasket(any(Basket.class));
            verify(orderService, never()).createOrder(any(Order.class), any(Customer.class));

        }


    }

}