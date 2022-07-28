package com.bersoft.bershopping.validators;

public abstract class AbstractValidator implements Validator {
    protected Validator nextValidator;
    protected String errorMessage = "still nothing validated";

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public boolean hasNext() {
        return nextValidator != null;
    }

    @Override
    public Validator setNext(Validator validator) {
        this.nextValidator = validator;
        return this.nextValidator;
    }

    @Override
    public Validator next() {
        return this.nextValidator;
    }

    @Override
    public Validator validateAll() {
        Validator validator = this;

        while (validator != null) {
            if (validator.next() == null || !validator.validate()) {
                break;
            }
            validator = validator.next();
        }

        return validator;
    }
}
