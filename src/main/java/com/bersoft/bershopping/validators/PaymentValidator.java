package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.customer.IPaymentMethod;

public class PaymentValidator extends AbstractValidator {

    private IPaymentMethod payMethod;
    private double amountToCharge;

    public PaymentValidator(IPaymentMethod paymentMethod, double amountToCharge) {
        this.payMethod = paymentMethod;
        this.amountToCharge = amountToCharge;
    }

    private boolean validatePaymentMethodIsAdded() {
        if (payMethod != null) {
            return true;
        } else {
            this.errorMessage = "add a payment method to the checkout";
            return false;
        }
    }

    private boolean validateBalanceGreaterThanAmount() {
        if (payMethod.getBalance() >= amountToCharge) {
            return true;
        } else {
            this.errorMessage = "payment method balance: " + payMethod.getBalance() + " not enough ";
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Balance validator: " + super.getErrorMessage();
    }

    @Override
    public boolean validate() {
        boolean value = validatePaymentMethodIsAdded() && validateBalanceGreaterThanAmount();
        if (value) {
            this.errorMessage = "everything goes well";
        }
        return value;
    }
}
