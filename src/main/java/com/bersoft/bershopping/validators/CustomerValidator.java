package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.customer.Customer;

public class CustomerValidator extends AbstractValidator {

    private Customer customer;

    public CustomerValidator(Customer customer) {
        this.customer = customer;
    }

    private boolean validateCustomerMustExist() {
        if (customer != null) {
            return true;
        }else{
            this.errorMessage = "set a customer is required to create a order";
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Customer validator: "+super.getErrorMessage();
    }

    @Override
    public boolean validate() {
        boolean value = validateCustomerMustExist();
        if (value) {
            this.errorMessage = "everything goes well";
        }
        return value;
    }
}
