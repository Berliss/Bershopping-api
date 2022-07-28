package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.customexceptions.MyBadOrderException;
import com.bersoft.bershopping.customexceptions.MyIdAndRequestBodyIdNotMatchException;
import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.utils.dtos.BasketDto;
import com.bersoft.bershopping.utils.dtos.ItemDto;
import com.bersoft.bershopping.utils.dtos.OrderDto;
import com.bersoft.bershopping.utils.mappers.BasketMapper;
import com.bersoft.bershopping.utils.mappers.ItemMapper;
import com.bersoft.bershopping.utils.dtos.ProductToAddDto;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.services.ICustomerService;
import com.bersoft.bershopping.services.IProductService;
import com.bersoft.bershopping.utils.mappers.OrderMapper;
import com.bersoft.bershopping.validators.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CheckoutRestController {

    private ICustomerService customerService;
    private IProductService productService;
    private String email = "berlisy2j@hotmail.com";

    public CheckoutRestController(ICustomerService customerService, IProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
    }

    @PostMapping("checkout")
    public ResponseEntity<?> createBasket(@RequestBody @Valid ProductToAddDto productToAdd) {
        //find the  customer mapped to this user
        Customer customer = customerService.findCustomerByEmail(email);

        //return a basket with a different status code, for the given case.
        return Optional.ofNullable(customer.getBasket())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {

                    Product product = productService.findById(productToAdd.id());
                    Basket basket = customerService.addProductToBasket(new Basket(), product, productToAdd.quantity());

                    basket.setCustomer(customer);
                    basket = customerService.createBasket(basket, customer);
                    return new ResponseEntity(new BasketMapper().mapBasketToBasketDto(basket), HttpStatus.CREATED);

                });
    }

    @GetMapping("checkout")
    public ResponseEntity<BasketDto> getBasket() {
        Basket basket = customerService.findCustomerBasket(email);
        return ResponseEntity.ok(new BasketMapper().mapBasketToBasketDto(basket));
    }

    @PutMapping("checkout/products/{id}")
    public ResponseEntity<List<ItemDto>> addItem(@RequestBody @Valid ProductToAddDto productToAdd, @PathVariable() Long id) {

        //check if body id & uri id match.
        if (!productToAdd.id().equals(id)) {
            throw new MyIdAndRequestBodyIdNotMatchException();
        }

        Product product = productService.findById(id);
        Basket basket = customerService.findCustomerBasket(email);
        int basketItemsSize = basket.getBasketItems().size();

        Basket updatedBasket = customerService.addProductToBasket(basket, product, productToAdd.quantity());
        updatedBasket = customerService.updateBasket(updatedBasket);
        ItemMapper mapper = new ItemMapper();

        //if same size then an item was updated else was created
        if (basketItemsSize == updatedBasket.getBasketItems().size()) {
            return ResponseEntity.ok(mapper.mapItemsToItemsDto(updatedBasket.getBasketItems()));
        } else {
            return new ResponseEntity<>(mapper.mapItemsToItemsDto(updatedBasket.getBasketItems()), HttpStatus.CREATED);
        }

    }

    @DeleteMapping("checkout/products/{id}")
    public ResponseEntity<List<ItemDto>> removeItem(@PathVariable Long id) {
        Product product = productService.findById(id);
        Basket basket = customerService.findCustomerBasket(email);

        Basket updatedBasket = customerService.removeProductFromBasket(basket, product);
        updatedBasket = customerService.updateBasket(updatedBasket);

        if (updatedBasket.getBasketItems().isEmpty()) {
            customerService.deleteBasket(updatedBasket);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(new ItemMapper().mapItemsToItemsDto(updatedBasket.getBasketItems()));
        }
    }

    @PutMapping("checkout/addresses/{id}")
    public ResponseEntity<Address> setAddress(@PathVariable Long id) {
        Basket basket = customerService.findCustomerBasket(email);
        boolean hasAddressAssigned = basket.getDelivery() != null;

        basket = customerService.setAddressToBasket(basket, id);
        basket = customerService.updateBasket(basket);

        if (hasAddressAssigned) {
            return ResponseEntity.ok(basket.getDelivery());
        } else {
            return new ResponseEntity<>(basket.getDelivery(), HttpStatus.CREATED);
        }
    }

    @PutMapping("checkout/payments/{id}")
    public ResponseEntity<PaymentMethod> setPaymentMethod(@PathVariable Long id) {
        Basket basket = customerService.findCustomerBasket(email);
        boolean hasPaymentAssigned = basket.getPaymentMethod() != null;

        basket = customerService.setPaymentMethodToBasket(basket, id);
        basket = customerService.updateBasket(basket);

        if (hasPaymentAssigned) {
            return ResponseEntity.ok(basket.getPaymentMethod());
        } else {
            return new ResponseEntity<>(basket.getPaymentMethod(), HttpStatus.CREATED);
        }
    }

    @PostMapping("orders")
    public ResponseEntity<OrderDto> createOrder() {

        Basket basket = customerService.findCustomerBasket(email);
        Customer customer = basket.getCustomer();

        Validator v1 = new CustomerValidator(customer);
        Validator v2 = new AddressValidator(basket.getDelivery());
        Validator v3 = new PaymentValidator(basket.getPaymentMethod(), basket.getTotal());
        Validator v4 = new ItemValidator(basket.getBasketItems());

        v1.setNext(v2);
        v2.setNext(v3);
        v3.setNext(v4);

        Validator result = v1.validateAll();

        if (result.validate()) {
            List<OrderItem> orderItemList = basket.getBasketItems()
                    .stream()
                    .map(item -> new OrderItem(item.getQuantity(), item.getPrice(), item.getProduct()))
                    .toList();

            //update products stock
            productService.updateProductStock(orderItemList);

            //debit the amount of the order, can be any pay method.
            basket.getPaymentMethod().debit(basket.getTotal());

            Order order = new Order();
            order.setCustomer(customer);
            order.setDelivery(basket.getDelivery());
            order.setPaymentMethod(basket.getPaymentMethod());
            order.setOrderItemList(orderItemList);

            Order createdOrder = customerService.createOrder(order);
            customerService.deleteBasket(basket);

            return new ResponseEntity<>(new OrderMapper().mapOrderToOrderDto(createdOrder), HttpStatus.CREATED);
        } else {
            throw new MyBadOrderException(result.getErrorMessage());
        }
    }

    //not homework related
    @GetMapping("customers")
    public ResponseEntity<Customer> getCustomerInfo() {
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    @GetMapping("customers/orders")
    public ResponseEntity<List<OrderDto>> getCustomerOrders() {

        OrderMapper mapper = new OrderMapper();

        List<OrderDto> orderList = customerService.findCustomerByEmail(email)
                .getOrders()
                .stream()
                .map(mapper::mapOrderToOrderDto)
                .toList();

        if (!orderList.isEmpty()) {
            return ResponseEntity.ok(orderList);
        } else {
            throw new MyResourceNotFoundException("customer does not have any order");
        }
    }
}
