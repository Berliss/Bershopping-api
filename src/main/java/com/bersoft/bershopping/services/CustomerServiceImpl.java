package com.bersoft.bershopping.services;

import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.customexceptions.MyStockNotEnoughException;
import com.bersoft.bershopping.persistence.entities.*;
import com.bersoft.bershopping.persistence.repositories.IBasketRepository;
import com.bersoft.bershopping.persistence.repositories.ICustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final ICustomerRepository customerRepository;
    private final IBasketRepository basketRepository;

    public CustomerServiceImpl(ICustomerRepository customerRepository, IBasketRepository basketRepository) {
        this.customerRepository = customerRepository;
        this.basketRepository = basketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() -> {
            throw new MyResourceNotFoundException("Customer with the given email '" + email + "' not found");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Basket findCustomerBasket(String email) {
        Customer c = this.findCustomerByEmail(email);
        return Optional.ofNullable(c.getBasket()).orElseThrow(() -> {
            throw new MyResourceNotFoundException("Checkout process is not initialized");
        });
    }

    @Override
    @Transactional
    public Basket createBasket(Basket basket, Customer customer) {
        if (basket != null && customer != null && customer.getId() != null) {
            basket.setCustomer(customer);
            return basketRepository.save(basket);
        } else {
            throw new NullPointerException("Basket, customer & customer id can not be null");
        }
    }

    @Override
    @Transactional
    public Basket updateBasket(Basket basket) {
        if (basket != null && basket.getId() != null) {
            return basketRepository.save(basket);
        } else {
            throw new NullPointerException("basket & basket id can not be null");
        }
    }

    @Override
    public Basket removeProductFromBasket(Basket basket, Product product) {
        if (basket != null && product != null && basket.getBasketItems() != null) {

            List<BasketItem> basketItems = basket.getBasketItems();

            for (BasketItem item : basketItems) {
                if (item.getProduct().equals(product)) {
                    basketItems.remove(item);
                    return basket;
                }
            }

            throw new MyResourceNotFoundException("This product is not inside the checkout");

        } else {
            throw new NullPointerException("basket, basket items & product can not be null");
        }
    }

    @Override
    public Basket addProductToBasket(Basket basket, Product product, Double quantity) {
        if (basket != null && basket.getBasketItems() != null && product != null && quantity != null) {

            //find inside the basket if found update quantity.
            List<BasketItem> basketItems = basket.getBasketItems();

            for (BasketItem item : basketItems) {
                if (item.getProduct().equals(product)) {
                    if (quantity <= item.getProduct().getStock()) {
                        item.setQuantity(quantity);
                        return basket;
                    } else {
                        throw new MyStockNotEnoughException(product, quantity);
                    }
                }
            }
            //if is not inside the basket add it
            basket.addItem(new BasketItem(quantity, product.getPrice(), product));
            return basket;

        } else {
            throw new NullPointerException("basket, basket items, product & quantity can not be null");
        }
    }

    @Override
    public Basket setPaymentMethodToBasket(Basket basket, Customer customer, Long paymentMethodId) {
        if (basket != null && customer != null && customer.getPaymentMethods() != null && paymentMethodId != null) {
            List<PaymentMethod> paymentMethods = customer.getPaymentMethods();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.getId().equals(paymentMethodId)) {
                    basket.setPaymentMethod(pm);
                    return basket;
                }
            }
            throw new MyResourceNotFoundException("this customer does not have a payment method with this id");
        }
        throw new NullPointerException("basket, customer, customer payment methods & payment method id can not be null");
    }


    @Override
    public Basket setAddressToBasket(Basket basket, Customer customer, Long addressId) {
        if (basket != null && customer != null && customer.getAddresses() != null && addressId != null) {
            List<Address> addresses = customer.getAddresses();
            for (Address address : addresses) {
                if (address.getId().equals(addressId)) {
                    basket.setAddress(address);
                    return basket;
                }
            }
            throw new MyResourceNotFoundException("this customer does not have an address with this id");
        }
        throw new NullPointerException("basket, customer, customer addresses & address id can not be null");
    }

    @Override
    @Transactional()
    public void deleteBasket(Basket basket) {
        basketRepository.deleteById(basket.getId());
    }


}
