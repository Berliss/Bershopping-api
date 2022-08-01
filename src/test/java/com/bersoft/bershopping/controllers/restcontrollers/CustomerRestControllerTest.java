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
import com.bersoft.bershopping.services.ICustomerService;
import com.bersoft.bershopping.utils.dtos.OrderDto;
import com.bersoft.bershopping.utils.mappers.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CustomerRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ICustomerService customerService;

    private ObjectMapper objectMapper;

    private Customer customer;

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

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("berlis");
        customer.setLastName("rodriguez");
        customer.setEmail("berlisy2j@hotmail.com");
        customer.setAddresses(Arrays.asList(address));
        customer.setPaymentMethods(Arrays.asList(pm));
        customer.setOrders(null);
        customer.setBasket(null);

        Product product = new Product(1L, 1000d, "shoes", 10d);

        Basket basket = new Basket();
        basket.setId(1L);
        basket.setCreateAt(new Date());
        basket.setCustomer(customer);
        basket.setDelivery(customer.getAddresses().get(0));
        basket.setPaymentMethod(customer.getPaymentMethods().get(0));
        basket.setBasketItems(Arrays.asList(new BasketItem(1d, product.getPrice(), product)));

        Order order = new Order();
        order.setId(1L);
        order.setCustomer(basket.getCustomer());
        order.setDelivery(basket.getDelivery());
        order.setCreateAt(new Date());
        order.setOrderItemList(Arrays.asList(new OrderItem(1d, product.getPrice(), product)));
        order.setPaymentMethod(basket.getCustomer().getPaymentMethods().get(0));

        //set the order
        customer.setOrders(Arrays.asList(order));

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/customers' endpoint METHOD:GET")
    class TestGetCustomerInfo {

        @Test
        @DisplayName("Test 'getCustomerInfo', expected -> 200 response status & info of the user found")
        void testGetCustomerInfo() throws Exception {

            // Given
            when(customerService.findCustomerByEmail(anyString())).thenReturn(customer);

            //when
            mvc.perform(get("/api/v1/bershopping/customers").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(customer)))
                    .andDo(print());

            verify(customerService).findCustomerByEmail(anyString());

        }

        @Test
        @DisplayName("Test 'getCustomerInfo' when customer not found, expected -> 404 response status & throws MyResourceNotFoundException ")
        void testGetCustomerInfoWhenNotFound() throws Exception {

            // Given
            when(customerService.findCustomerByEmail(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(get("/api/v1/bershopping/customers").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(customerService).findCustomerByEmail(anyString());

        }

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/customers/orders' endpoint METHOD:GET")
    class TestGetCustomerOrders {

        @Test
        @DisplayName("Test 'getCustomerOrders', expected -> 200 response status & list of orders")
        void testGetCustomerOrders() throws Exception {

            // Given
            OrderMapper mapper = new OrderMapper();
            when(customerService.findCustomerByEmail(anyString())).thenReturn(customer);
            OrderDto orderDto = mapper.mapOrderToOrderDto(customer.getOrders().get(0));

            //when
            mvc.perform(get("/api/v1/bershopping/customers/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(orderDto))))
                    .andDo(print());

            verify(customerService).findCustomerByEmail(anyString());

        }

        @Test
        @DisplayName("Test 'getCustomerOrders' when customer does not have any order, expected -> 200 response status & empty list ")
        void testGetCustomerOrdersWhenDoesNotHaveAnyOrder() throws Exception {

            // Given
            customer.setOrders(new ArrayList<>());
            when(customerService.findCustomerByEmail(anyString())).thenReturn(customer);

            //when
            mvc.perform(get("/api/v1/bershopping/customers/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<>())))
                    .andDo(print());

            verify(customerService).findCustomerByEmail(anyString());

        }

        @Test
        @DisplayName("Test 'getCustomerOrders' when customer not found, expected -> 404 response status & throws MyResourceNotFoundException ")
        void testGetCustomerOrdersWhenCustomerDoesNotExist() throws Exception {

            // Given
            when(customerService.findCustomerByEmail(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(get("/api/v1/bershopping/customers/orders").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(customerService).findCustomerByEmail(anyString());

        }

    }
}