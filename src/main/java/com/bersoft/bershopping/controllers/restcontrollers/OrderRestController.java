package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.customexceptions.MyBadOrderException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.checkout.Order;
import com.bersoft.bershopping.persistence.entities.checkout.OrderItem;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.services.*;
import com.bersoft.bershopping.utils.ClaimsDecoder;
import com.bersoft.bershopping.utils.dtos.OrderDto;
import com.bersoft.bershopping.utils.mappers.OrderMapper;
import com.bersoft.bershopping.validators.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bershopping/orders")
public class OrderRestController {

    private ICheckoutService checkoutService;
    private IProductService productService;
    private IOrderService orderService;

    public OrderRestController(ICheckoutService checkoutService, IProductService productService, IOrderService orderService) {
        this.checkoutService = checkoutService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        Basket basket = checkoutService.findCustomerBasket(email);
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
            order.setDelivery(basket.getDelivery());
            order.setPaymentMethod(basket.getPaymentMethod());
            order.setOrderItemList(orderItemList);

            Order createdOrder = orderService.createOrder(order,customer);
            checkoutService.deleteBasket(basket);

            return new ResponseEntity<>(new OrderMapper().mapOrderToOrderDto(createdOrder), HttpStatus.CREATED);
        } else {
            throw new MyBadOrderException(result.getErrorMessage());
        }
    }
}
