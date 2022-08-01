package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.customexceptions.MyIdAndRequestBodyIdNotMatchException;
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
import com.bersoft.bershopping.services.ICustomerService;
import com.bersoft.bershopping.services.IProductService;
import com.bersoft.bershopping.utils.dtos.ProductToAddDto;
import com.bersoft.bershopping.utils.mappers.BasketMapper;
import com.bersoft.bershopping.utils.mappers.ItemMapper;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ICheckoutService checkoutService;

    @MockBean
    private IProductService productService;

    @MockBean
    private ICustomerService customerService;

    private ObjectMapper objectMapper;

    private Basket basket;

    private Customer customer;

    private Product product;

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

        customer.setBasket(basket);


    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/checkout' endpoint METHOD:GET")
    class TestGetCheckout {

        @Test
        @DisplayName("Test 'getCheckout', expected -> 200 response status & the current checkout")
        void getCheckout() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);

            //when
            mvc.perform(get("/api/v1/bershopping/checkout").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(new BasketMapper().mapBasketToBasketDto(basket))))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());

        }

        @Test
        @DisplayName("Test 'getCheckout' when checkout does not exist, expected -> 404 response status & throws MyResourceNotFoundException")
        void getCheckoutWhenCheckoutDoesNotExist() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(get("/api/v1/bershopping/checkout").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());

        }

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/checkout/products/{id}' endpoint METHOD:PUT")
    class TestAddItem {

        @Test
        @DisplayName("Test 'addItem' when checkout does not exist, expected -> 201 response status & create a checkout & return checkout items")
        void testAddItemWhenProductDoesNotExist() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(1L, 2d);

            when(productService.findById(anyLong())).thenReturn(product);
            when(customerService.findCustomerByEmail(anyString())).thenReturn(new Customer());
            when(checkoutService.addProductToBasket(any(Basket.class), any(Product.class), anyDouble())).thenReturn(new Basket());

            //the item is preset on setup to reduce boilerplate code
            when(checkoutService.createBasket(any(Basket.class), any(Customer.class))).thenAnswer(invocation -> {
                basket.getBasketItems().get(0).setQuantity(productToAddDto.quantity());
                return basket;
            });


            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(content().json(objectMapper.writeValueAsString(new ItemMapper().mapItemsToItemsDto(basket.getBasketItems()))))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(customerService).findCustomerByEmail(anyString());
            verify(checkoutService).addProductToBasket(any(), any(), any());
            verify(checkoutService).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService, never()).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when checkout exist & product is already added, expected -> 200 response status & return checkout items with quantity edited")
        void testAddItemWhenCheckoutExistAndProductIsInsideBasket() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(1L, 2d);

            when(productService.findById(anyLong())).thenReturn(product);
            when(customerService.findCustomerByEmail(anyString())).thenReturn(customer);
            when(checkoutService.addProductToBasket(any(Basket.class), any(Product.class), anyDouble())).thenReturn(basket);
            when(checkoutService.updateBasket(any(Basket.class))).thenAnswer(invocation -> {
                basket.getBasketItems().get(0).setQuantity(productToAddDto.quantity());
                return basket;
            });


            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(content().json(objectMapper.writeValueAsString(new ItemMapper().mapItemsToItemsDto(basket.getBasketItems()))))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(customerService).findCustomerByEmail(anyString());
            verify(checkoutService).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when checkout exist & product is not added, expected -> 201 response status & return checkout items with new product added")
        void testAddItemWhenCheckoutExistAndProductIsNotInsideBasket() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(2L, 2d);

            //new product to be added
            Product anotherProduct = new Product();
            anotherProduct.setId(productToAddDto.id());
            anotherProduct.setStock(10d);
            anotherProduct.setPrice(500d);
            anotherProduct.setDescription("laptop");

            when(productService.findById(anyLong())).thenReturn(product);
            when(customerService.findCustomerByEmail(anyString())).thenReturn(customer);
            when(checkoutService.addProductToBasket(any(Basket.class), any(Product.class), anyDouble())).thenReturn(basket);
            when(checkoutService.updateBasket(any(Basket.class))).thenAnswer(invocation -> {
                basket.getBasketItems().add(new BasketItem(productToAddDto.quantity(), anotherProduct.getPrice(), anotherProduct));
                return basket;
            });


            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(content().json(objectMapper.writeValueAsString(new ItemMapper().mapItemsToItemsDto(basket.getBasketItems()))))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(customerService).findCustomerByEmail(anyString());
            verify(checkoutService).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when body id & id on url does not match, expected -> 400 response status & throws MyIdAndRequestBodyIdNotMatchException")
        void testAddItemWhenBodyIdAndIdOnUrlDoesNotMatch() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(2L, 2d);

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/453")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyIdAndRequestBodyIdNotMatchException))
                    .andDo(print());

            verify(productService, never()).findById(anyLong());
            verify(customerService, never()).findCustomerByEmail(anyString());
            verify(checkoutService, never()).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService, never()).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when product is not found, expected -> 404 response status & throws MyResourceNotFoundException")
        void testAddItemWhenProductIsNotFound() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(2L, 2d);
            when(productService.findById(anyLong())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(customerService, never()).findCustomerByEmail(anyString());
            verify(checkoutService, never()).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService, never()).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when checkout is not found, expected -> 404 response status & throws MyResourceNotFoundException")
        void testAddItemWhenCheckoutIsNotFound() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(2L, 2d);
            when(productService.findById(anyLong())).thenReturn(new Product());
            when(customerService.findCustomerByEmail(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(customerService).findCustomerByEmail(anyString());
            verify(checkoutService, never()).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService, never()).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'addItem' when body is not valid, expected -> 400 response status & throws MethodArgumentNotValidException")
        void testAddItemWhenBodyIsNotValid() throws Exception {

            // Given
            ProductToAddDto productToAddDto = new ProductToAddDto(null, null);

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/products/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productToAddDto)))

                    //then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                    .andDo(print());

            verify(productService, never()).findById(anyLong());
            verify(customerService, never()).findCustomerByEmail(anyString());
            verify(checkoutService, never()).addProductToBasket(any(), any(), any());
            verify(checkoutService, never()).createBasket(any(Basket.class), any(Customer.class));
            verify(checkoutService, never()).updateBasket(any());

        }

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/checkout/products/{id}' endpoint METHOD:DELETE")
    class TestRemoveItem {

        @Test
        @DisplayName("Test 'removeItem' when checkouts products gets empty, expected -> 204 response status")
        void testRemoveItemWhenRemoveAllItemsDeleteCheckout() throws Exception {

            // Given
            when(productService.findById(anyLong())).thenReturn(product);
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.removeProductFromBasket(basket, product)).thenAnswer(invocation -> {
                basket.getBasketItems().remove(0);
                return basket;
            });

            //when
            mvc.perform(delete("/api/v1/bershopping/checkout/products/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).removeProductFromBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'removeItem' when checkout still has products left, expected -> 200 response status & return checkout products")
        void testRemoveItem() throws Exception {

            // Given
            when(productService.findById(anyLong())).thenReturn(product);
            when(checkoutService.findCustomerBasket(anyString())).thenAnswer(invocation -> {
                basket.getBasketItems().add(new BasketItem());
                return basket;
            });
            when(checkoutService.removeProductFromBasket(basket, product)).thenAnswer(invocation -> {
                basket.getBasketItems().remove(1);
                return basket;
            });

            //when
            mvc.perform(delete("/api/v1/bershopping/checkout/products/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(new ItemMapper().mapItemsToItemsDto(basket.getBasketItems()))))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).removeProductFromBasket(any(), any());
            verify(checkoutService).updateBasket(any());

        }

        @Test
        @DisplayName("Test 'removeItem' when product is not inside the checkout items, expected -> 404 response status & throw MyResourceNotFoundException")
        void testRemoveItemWhenProductIsNotInsideItemsList() throws Exception {

            // Given
            when(productService.findById(anyLong())).thenReturn(product);
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.removeProductFromBasket(basket, product)).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(delete("/api/v1/bershopping/checkout/products/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(productService).findById(anyLong());
            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).removeProductFromBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());

        }

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/checkout/addresses/{id}' endpoint METHOD:PUT")
    class TestSetAddress {

        @Test
        @DisplayName("Test 'setAddress', expected -> 200 response status & return the address added")
        void testSetAddress() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.setAddressToBasket(basket,1L)).thenReturn(basket);
            when(checkoutService.updateBasket(any(Basket.class))).thenReturn(basket);

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(basket.getDelivery())))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).setAddressToBasket(any(), any());
            verify(checkoutService).updateBasket(any());
        }

        @Test
        @DisplayName("Test 'setAddress' when customer does not have the given address, expected -> 404 response status & throws MyResourceNotFoundException")
        void testSetAddressWhenCustomerDoesNotHaveGivenAddress() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.setAddressToBasket(basket,1L)).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).setAddressToBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());
        }

        @Test
        @DisplayName("Test 'setAddress' when customer not found, expected -> 404 response status & throws MyResourceNotFoundException")
        void testSetAddressWhenCustomerNotFound() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService, never()).setAddressToBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());
        }

    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/checkout/payments/{id}' endpoint METHOD:PUT")
    class TestSetPaymentMethod {

        @Test
        @DisplayName("Test 'setPaymentMethod', expected -> 200 response status & return the payment method added")
        void testSetPaymentMethod() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.setPaymentMethodToBasket(basket,1L)).thenReturn(basket);
            when(checkoutService.updateBasket(any(Basket.class))).thenReturn(basket);

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/payments/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(basket.getPaymentMethod())))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).setPaymentMethodToBasket(any(), any());
            verify(checkoutService).updateBasket(any());
        }

        @Test
        @DisplayName("Test 'setPaymentMethod' when customer does not have the given payment method, expected -> 404 response status & throws MyResourceNotFoundException")
        void testSetPaymentMethodWhenCustomerDoesNotHaveGivenAddress() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenReturn(basket);
            when(checkoutService.setPaymentMethodToBasket(basket,1L)).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/payments/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService).setPaymentMethodToBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());
        }

        @Test
        @DisplayName("Test 'setPaymentMethod' when customer not found, expected -> 404 response status & throws MyResourceNotFoundException")
        void testSetPaymentMethodWhenCustomerNotFound() throws Exception {

            // Given
            when(checkoutService.findCustomerBasket(anyString())).thenThrow(new MyResourceNotFoundException(""));

            //when
            mvc.perform(put("/api/v1/bershopping/checkout/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MyResourceNotFoundException))
                    .andDo(print());

            verify(checkoutService).findCustomerBasket(anyString());
            verify(checkoutService, never()).setPaymentMethodToBasket(any(), any());
            verify(checkoutService, never()).updateBasket(any());
        }

    }


}