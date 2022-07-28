package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.customer.Address;

public class AddressValidator extends AbstractValidator {

    private Address address;

    public AddressValidator(Address address) {
        this.address = address;
    }

    private boolean validateAddressMustExist() {
        if (address != null) {
            return true;
        }else{
            this.errorMessage = "delivery address is required to create a order";
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Address validator: "+super.getErrorMessage();
    }

    @Override
    public boolean validate() {
        boolean value = validateAddressMustExist();
        if (value) {
            this.errorMessage = "everything goes well";
        }
        return value;
    }
}
