package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.customexceptions.MyIdAndRequestBodyIdNotMatchException;
import com.bersoft.bershopping.persistence.entities.checkout.Basket;
import com.bersoft.bershopping.persistence.entities.customer.Address;
import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.persistence.entities.customer.PaymentMethod;
import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.services.ICheckoutService;
import com.bersoft.bershopping.services.ICustomerService;
import com.bersoft.bershopping.services.IProductService;
import com.bersoft.bershopping.utils.ClaimsDecoder;
import com.bersoft.bershopping.utils.dtos.BasketDto;
import com.bersoft.bershopping.utils.dtos.ItemDto;
import com.bersoft.bershopping.utils.dtos.ProductToAddDto;
import com.bersoft.bershopping.utils.mappers.BasketMapper;
import com.bersoft.bershopping.utils.mappers.ItemMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bershopping/checkout")
public class CheckoutRestController {

    private ICheckoutService checkoutService;
    private IProductService productService;
    private ICustomerService customerService;

    public CheckoutRestController(ICheckoutService checkoutService, IProductService productService, ICustomerService customerService) {
        this.checkoutService = checkoutService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @GetMapping()
    public ResponseEntity<BasketDto> getCheckout(JwtAuthenticationToken principal) {
        String email = ClaimsDecoder.getEmailFromClaims(principal);
        Basket basket = checkoutService.findCustomerBasket(email);
        return ResponseEntity.ok(new BasketMapper().mapBasketToBasketDto(basket));
    }

    @PutMapping("products/{id}")
    public ResponseEntity<List<ItemDto>> addItem(@RequestBody @Valid ProductToAddDto productToAdd, @PathVariable() Long id, JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        //check if body id & uri id match.
        if (!productToAdd.id().equals(id)) {
            throw new MyIdAndRequestBodyIdNotMatchException();
        }

        Product product = productService.findById(id);
        Customer customer = customerService.findCustomerByEmail(email);
        Basket basket = customer.getBasket();
        ItemMapper mapper = new ItemMapper();

        //if the basket does not exist, create one & add the product then return the basket items
        if (basket == null) {
            basket = checkoutService.addProductToBasket(new Basket(), product, productToAdd.quantity());
            basket = checkoutService.createBasket(basket, customer);
            return new ResponseEntity<>(mapper.mapItemsToItemsDto(basket.getBasketItems()), HttpStatus.CREATED);
        }

        //if we get here then a basket already exist
        //get the current size of items inside the basket
        int basketItemsSize = basket.getBasketItems().size();

        //add or modify the item & then update de basket
        checkoutService.addProductToBasket(basket, product, productToAdd.quantity());
        checkoutService.updateBasket(basket);
        List<ItemDto> basketItems = mapper.mapItemsToItemsDto(basket.getBasketItems());


        //if same size then an item was updated else was created
        if (basketItemsSize == basket.getBasketItems().size()) {
            return ResponseEntity.ok(basketItems);
        } else {
            return new ResponseEntity<>(basketItems, HttpStatus.CREATED);
        }

    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<List<ItemDto>> removeItem(@PathVariable Long id, JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        Product product = productService.findById(id);
        Basket basket = checkoutService.findCustomerBasket(email);

        checkoutService.removeProductFromBasket(basket, product);

        if (!basket.getBasketItems().isEmpty()) {
            checkoutService.updateBasket(basket);
            return ResponseEntity.ok(new ItemMapper().mapItemsToItemsDto(basket.getBasketItems()));
        } else {
            checkoutService.deleteBasket(basket);
            return  new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("addresses/{id}")
    public ResponseEntity<Address> setAddress(@PathVariable Long id, JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        Basket basket = checkoutService.findCustomerBasket(email);
        checkoutService.setAddressToBasket(basket, id);
        checkoutService.updateBasket(basket);

        return ResponseEntity.ok(basket.getDelivery());
    }

    @PutMapping("payments/{id}")
    public ResponseEntity<PaymentMethod> setPaymentMethod(@PathVariable Long id, JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        Basket basket = checkoutService.findCustomerBasket(email);
        checkoutService.setPaymentMethodToBasket(basket, id);
        checkoutService.updateBasket(basket);

        return ResponseEntity.ok(basket.getPaymentMethod());
    }

}
