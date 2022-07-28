package com.bersoft.bershopping.validators;

import com.bersoft.bershopping.persistence.entities.checkout.BasketItem;

import java.util.List;

public class ItemValidator extends AbstractValidator {

    private List<BasketItem> basketItemList;

    public ItemValidator(List<BasketItem> basketItemList) {
        this.basketItemList = basketItemList;
    }

    private boolean validateAtLeastOneItemExist() {
        if (basketItemList != null && !basketItemList.isEmpty()) {
            return true;
        }else{
            this.errorMessage = "at least one item needed";
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "Items validator: "+super.getErrorMessage();
    }

    @Override
    public boolean validate() {
        boolean value = validateAtLeastOneItemExist();
        if (value) {
            this.errorMessage = "everything goes well";
        }
        return value;
    }
}
