package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.persistence.entities.customer.Customer;
import com.bersoft.bershopping.services.ICustomerService;
import com.bersoft.bershopping.utils.ClaimsDecoder;
import com.bersoft.bershopping.utils.dtos.OrderDto;
import com.bersoft.bershopping.utils.mappers.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//not homework related
@RestController
@RequestMapping("/api/v1/bershopping/customers")
public class CustomerRestController {

    ICustomerService customerService;

    public CustomerRestController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public ResponseEntity<Customer> getCustomerInfo(JwtAuthenticationToken principal) {
        String email = ClaimsDecoder.getEmailFromClaims(principal);
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    @GetMapping("orders")
    public ResponseEntity<List<OrderDto>> getCustomerOrders(JwtAuthenticationToken principal) {

        String email = ClaimsDecoder.getEmailFromClaims(principal);
        OrderMapper mapper = new OrderMapper();

        List<OrderDto> orderList = customerService.findCustomerByEmail(email)
                .getOrders()
                .stream()
                .map(mapper::mapOrderToOrderDto)
                .toList();


            return ResponseEntity.ok(orderList);
    }
}
