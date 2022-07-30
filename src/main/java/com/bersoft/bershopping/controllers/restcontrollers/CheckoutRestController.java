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
import java.util.Optional;

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

    @PostMapping()
    public ResponseEntity<?> createBasket(@RequestBody @Valid ProductToAddDto productToAdd, JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);

        //find the  customer mapped to this user
        Customer customer = customerService.findCustomerByEmail(email);

        //return a basket with a different status code, for the given case.
        return Optional.ofNullable(customer.getBasket())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {

                    Product product = productService.findById(productToAdd.id());
                    Basket basket = checkoutService.addProductToBasket(new Basket(), product, productToAdd.quantity());

                    basket.setCustomer(customer);
                    basket = checkoutService.createBasket(basket, customer);
                    return new ResponseEntity(new BasketMapper().mapBasketToBasketDto(basket), HttpStatus.CREATED);

                });
    }

    @GetMapping()
    public ResponseEntity<BasketDto> getBasket(JwtAuthenticationToken principal) {
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
        Basket basket = checkoutService.findCustomerBasket(email);
        int basketItemsSize = basket.getBasketItems().size();
        ItemMapper mapper = new ItemMapper();

        checkoutService.addProductToBasket(basket, product, productToAdd.quantity());
        checkoutService.updateBasket(basket);

        //if same size then an item was updated else was created
        if (basketItemsSize == basket.getBasketItems().size()) {
            return ResponseEntity.ok(mapper.mapItemsToItemsDto(basket.getBasketItems()));
        } else {
            return new ResponseEntity<>(mapper.mapItemsToItemsDto(basket.getBasketItems()), HttpStatus.CREATED);
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
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
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
