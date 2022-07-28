package com.bersoft.bershopping.validators;

public interface Validator {

    String getErrorMessage();

    boolean validate();

    boolean hasNext();

    Validator next();

    Validator setNext(Validator validator);

    Validator validateAll();

}
