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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CheckoutServiceImpl implements ICheckoutService {

    private ICustomerRepository customerRepository;
    private IBasketRepository basketRepository;

    public CheckoutServiceImpl(ICustomerRepository customerRepository, IBasketRepository basketRepository) {
        this.customerRepository = customerRepository;
        this.basketRepository = basketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Basket findCustomerBasket(String email) {
        Customer c = customerRepository.findByEmail(email).orElseThrow(() -> {
            throw new MyResourceNotFoundException("customer with the given email '" + email + "' not found");
        });

        return Optional.ofNullable(c.getBasket()).orElseThrow(() -> {
            throw new MyResourceNotFoundException("checkout process is not initialized");
        });
    }

    @Override
    @Transactional
    public Basket createBasket(Basket basket, Customer customer) {
        if (basket != null && customer != null && customer.getId() != null && basket.getId() == null) {
            basket.setCustomer(customer);
            return basketRepository.save(basket);
        } else {
            throw new IllegalArgumentException("basket, customer, customer id & basket id can not be null");
        }
    }

    @Override
    @Transactional
    public Basket updateBasket(Basket basket) {
        if (basket != null && basket.getId() != null && basketRepository.existsById(basket.getId())) {
            return basketRepository.save(basket);
        } else {
            throw new MyResourceNotFoundException("basket not found, can not execute update");
        }
    }

    @Override
    public Basket removeProductFromBasket(Basket basket, Product product) {
        List<BasketItem> basketItems = basket.getBasketItems();
        for (BasketItem item : basketItems) {
            if (item.getProduct().equals(product)) {
                basketItems.remove(item);
                return basket;
            }
        }
        throw new MyResourceNotFoundException("product is not inside the checkout");
    }

    @Override
    public Basket addProductToBasket(Basket basket, Product product, Double quantity) {
        //find inside the basket if found update quantity.
        List<BasketItem> basketItems = basket.getBasketItems();
        for (BasketItem item : basketItems) {
            if (item.getProduct().equals(product)) {
                return updateProductQuantity(basket, product, quantity, item);
            }
        }
        //if is not inside the basket add it
        return addNewProductToBasket(basket, product, quantity);
    }

    @Override
    public Basket setPaymentMethodToBasket(Basket basket, Long paymentMethodId) {
        List<PaymentMethod> paymentMethods = basket.getCustomer().getPaymentMethods();
        for (PaymentMethod pm : paymentMethods) {
            if (pm.getId().equals(paymentMethodId)) {
                basket.setPaymentMethod(pm);
                return basket;
            }
        }
        throw new MyResourceNotFoundException("this customer does not have a payment method with this id");
    }

    @Override
    public Basket setAddressToBasket(Basket basket, Long addressId) {
        List<Address> addresses = basket.getCustomer().getAddresses();
        for (Address address : addresses) {
            if (address.getId().equals(addressId)) {
                basket.setDelivery(address);
                return basket;
            }
        }
        throw new MyResourceNotFoundException("this customer does not have an address with this id");
    }

    @Override
    @Transactional()
    public void deleteBasket(Basket basket) {
        basketRepository.deleteById(basket.getId());
    }

    //private methods for internal use.
    private Basket updateProductQuantity(Basket basket, Product product, Double quantity, BasketItem item) {
        if (quantity <= item.getProduct().getStock() && quantity > 0 && product.getStock() > 0) {
            item.setQuantity(quantity);
            return basket;
        } else {
            throw new MyStockNotEnoughException(product, quantity);
        }
    }

    private Basket addNewProductToBasket(Basket basket, Product product, Double quantity) {
        if (quantity <= product.getStock() && quantity > 0 && product.getStock() > 0) {
            basket.addItem(new BasketItem(quantity, product.getPrice(), product));
            return basket;
        } else {
            throw new MyStockNotEnoughException(product, quantity);
        }
    }

}
