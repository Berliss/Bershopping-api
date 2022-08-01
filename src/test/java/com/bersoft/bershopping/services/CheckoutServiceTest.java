package com.bersoft.bershopping.services;

import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.customexceptions.MyStockNotEnoughException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.persistence.repositories.IBasketRepository;
import com.bersoft.bershopping.persistence.repositories.ICustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IBasketRepository basketRepository;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private Basket basket;

    private Customer customer;

    private Product product;

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

        customer.setBasket(basket);


    }

    @Nested
    @DisplayName("Tests on 'findCustomerBasket' ")
    class TestsFindCustomerBasket {

        @Test
        @DisplayName("Test 'findCustomerBasket', expected -> return not null basket.")
        void testFindCustomerBasket() {

            //given
            when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

            //when
            Basket customerBasket = checkoutService.findCustomerBasket(anyString());

            //then
            assertNotNull(customerBasket);

        }

        @Test
        @DisplayName("Test 'findCustomerBasket' when customer does not have basket, expected -> throws MyResourceNotFoundException.")
        void testFindCustomerBasketWhenCustomerDoesNotHaveBasket() {

            //given
            customer.setBasket(null);
            when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

            //when
            // then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.findCustomerBasket(anyString()));

        }

        @Test
        @DisplayName("Test 'findCustomerBasket' when customer does not exist, expected -> throws MyResourceNotFoundException.")
        void testFindCustomerBasketWhenCustomerDoesNotExist() {

            //given
            when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //when
            // then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.findCustomerBasket(anyString()));

        }

    }

    @Nested
    @DisplayName("Tests on 'createBasket' ")
    class TestsCreateBasket {

        @Test
        @DisplayName("Test 'createBasket', expected -> return a basket with a id.")
        void testCreateBasket() {

            //given
            basket.setId(null);
            when(basketRepository.save(basket)).thenAnswer(invocation -> {
                basket.setId(3L);
                return basket;
            });

            //when
            Basket customerBasket = checkoutService.createBasket(basket, customer);

            //then
            assertNotNull(customerBasket);
            assertNotNull(customerBasket.getId());
            assertEquals(3L,customerBasket.getId());

        }

        @Test
        @DisplayName("Test 'findCustomerBasket' when invalids arguments, expected -> throws IllegalArgumentException.")
        void testCreateBasketWhenInvalidValuesOnArguments() {

            //given
            //when
            //then
            assertThrows(IllegalArgumentException.class, () -> checkoutService.createBasket(null, null));

            verify(basketRepository, never()).save(any());

        }

    }

    @Nested
    @DisplayName("Tests on 'updateBasket' ")
    class TestsUpdateBasket {

        @Test
        @DisplayName("Test 'updateBasket', expected -> return a basket with new values.")
        void testUpdateBasket() {

            //given
            long oldId = basket.getId();
            int oldItemsSize = basket.getBasketItems().size();

            when(basketRepository.existsById(anyLong())).thenReturn(true);
            when(basketRepository.save(basket)).thenAnswer(invocation -> {
                basket.setId(3L);
                basket.getBasketItems().add(new BasketItem());
                return basket;
            });

            //when
            Basket customerBasket = checkoutService.updateBasket(basket);

            //then
            assertNotNull(customerBasket);
            assertNotNull(customerBasket.getId());
            assertNotEquals(oldId,customerBasket.getId());
            assertNotEquals(oldItemsSize,customerBasket.getBasketItems().size());

        }

        @Test
        @DisplayName("Test 'updateBasket' when invalids arguments, expected -> throws MyResourceNotFoundException.")
        void testUpdateBasketWhenInvalidValuesOnArguments() {

            //given
            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.updateBasket(null));

            verify(basketRepository, never()).existsById(anyLong());
            verify(basketRepository, never()).save(any());

        }

        @Test
        @DisplayName("Test 'updateBasket' when basket does not exist, expected -> throws MyResourceNotFoundException.")
        void testUpdateBasketWhenBasketDoesNotExist() {

            //given
            when(basketRepository.existsById(anyLong())).thenReturn(false);

            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.updateBasket(basket));

            verify(basketRepository).existsById(anyLong());
            verify(basketRepository, never()).save(any());

        }

    }

    @Nested
    @DisplayName("Tests on 'removeProductFromBasket' ")
    class TestRemoveProductFromBasket{
        
        @Test
        @DisplayName("Test 'removeProductFromBasket', expected -> return a basket with a item less")
        void testRemoveProductFromBasket() {
            
            //given
            int olditemsSize = basket.getBasketItems().size();
            
            //when
            Basket customerBasket = checkoutService.removeProductFromBasket(basket, product);
            
            //then
            assertTrue(customerBasket.getBasketItems().size() < olditemsSize);
            
        }

        @Test
        @DisplayName("Test 'removeProductFromBasket' when product not inside basket, expected -> throws MyResourceNotFoundException")
        void testRemoveProductFromBasketWhenProductIsNotInside() {

            //given
            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.removeProductFromBasket(basket, new Product()));

        }
        
    }

    @Nested
    @DisplayName("Tests on 'setPaymentMethodToBasket' ")
    class TestSetPaymentMethodToBasket{

        @Test
        @DisplayName("Test 'setPaymentMethodToBasket', expected -> return a basket with the given payment method")
        void testSetPaymentMethodToBasket() {

            //given
            PaymentMethod pm = customer.getPaymentMethods().get(0);
            pm.setBalance(88888d);

            //when
            Basket customerBasket = checkoutService.setPaymentMethodToBasket(basket, pm.getId());

            //then
            assertEquals(pm.getBalance(),customerBasket.getPaymentMethod().getBalance());

        }

        @Test
        @DisplayName("Test 'setPaymentMethodToBasket' when payment method is not found in customer payment methods, expected -> throws MyResourceNotFoundException")
        void testSetPaymentMethodToBasketWhenPaymentMethodIsNotFound() {

            //given
            PaymentMethod pm = new PaymentMethod();
            pm.setId(45L);

            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.setPaymentMethodToBasket(basket, pm.getId()));

        }

    }

    @Nested
    @DisplayName("Tests on 'setAddressToBasket' ")
    class TestSetAddressToBasket{

        @Test
        @DisplayName("Test 'setAddressToBasket', expected -> return a basket with the given address")
        void testSetAddressToBasket() {

            //given
            Address address = customer.getAddresses().get(0);
            address.setDescription("dominican republic");

            //when
            Basket customerBasket = checkoutService.setAddressToBasket(basket, address.getId());

            //then
            assertEquals(address.getDescription(),customerBasket.getDelivery().getDescription());

        }

        @Test
        @DisplayName("Test 'setAddressToBasket' when address is not found in customer addresses, expected -> throws MyResourceNotFoundException")
        void testSetAddressToBasketWhenAddressIsNotFound() {

            //given
            Address address = new Address();
            address.setId(433L);

            //when
            //then
            assertThrows(MyResourceNotFoundException.class, () -> checkoutService.setAddressToBasket(basket, address.getId()));

        }

    }

    @Nested
    @DisplayName("Tests on 'addProductToBasket' ")
    class TestAddProductToBasket{

        @Test
        @DisplayName("Test 'addProductToBasket' when product is inside basket, expected -> return a basket with product item edited")
        void testAddProductToBasketWhenProductIsInside() {
            //given
            int oldItemsSize = basket.getBasketItems().size();
            double oldQty = basket.getBasketItems().get(0).getQuantity();

            //when
            Basket customerBasket = checkoutService.addProductToBasket(basket, product, 3d);

            //then
            assertEquals(oldItemsSize, customerBasket.getBasketItems().size() );
            assertNotEquals(oldQty, customerBasket.getBasketItems().get(0).getQuantity() );

        }

        @Test
        @DisplayName("Test 'addProductToBasket' when product is not inside basket, expected -> return a basket with a new product added")
        void testAddProductToBasketWhenProductIsNotInside() {

            //given
            int oldItemsSize = basket.getBasketItems().size();

            Product productToAdd = new Product();
            productToAdd.setDescription("table");
            productToAdd.setStock(10d);
            productToAdd.setId(7L);
            productToAdd.setPrice(400d);

            //when
            Basket customerBasket = checkoutService.addProductToBasket(basket, productToAdd, 3d);

            //then
            assertTrue(oldItemsSize < customerBasket.getBasketItems().size() );

        }

        @Test
        @DisplayName("Test 'addProductToBasket' when product is not inside basket but not enough stock, expected -> throws MyStockNotEnoughException")
        void testAddProductToBasketWhenProductIsNotInsideAndStockNotEnough() {

            //given
            Product productToAdd = new Product();
            productToAdd.setDescription("table");
            productToAdd.setStock(10d);
            productToAdd.setId(7L);
            productToAdd.setPrice(400d);

            //when
            //then
            assertThrows(MyStockNotEnoughException.class,() -> checkoutService.addProductToBasket(basket, productToAdd, 11d));

        }

        @Test
        @DisplayName("Test 'addProductToBasket' when product is inside basket but not enough stock, expected -> throws MyStockNotEnoughException")
        void testAddProductToBasket() {

            //given
            //when
            //then
            assertThrows(MyStockNotEnoughException.class,() -> checkoutService.addProductToBasket(basket, product, 11d));

        }

    }


}