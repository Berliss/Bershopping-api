package com.bersoft.bershopping.controllers.exceptioncontrollers;


import com.bersoft.bershopping.customexceptions.MyBadOrderException;
import com.bersoft.bershopping.customexceptions.MyIdAndRequestBodyIdNotMatchException;
import com.bersoft.bershopping.customexceptions.MyResourceNotFoundException;
import com.bersoft.bershopping.customexceptions.MyStockNotEnoughException;
import com.bersoft.bershopping.utils.ApiErrorResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return createErrorResponse("Server error", ex, HttpStatus.INTERNAL_SERVER_ERROR, true);
    }

    @ExceptionHandler(MyResourceNotFoundException.class)
    public final ResponseEntity<Object> handleMyUserNotFoundException(Exception ex, WebRequest request) {
        return createErrorResponse("Resource not found", ex, HttpStatus.NOT_FOUND, false);
    }

    @ExceptionHandler({MyStockNotEnoughException.class, MyIdAndRequestBodyIdNotMatchException.class, MyBadOrderException.class})
    public final ResponseEntity<Object> hanldleBadRequestExceptions(Exception ex, WebRequest request) {
        return createErrorResponse("Bad request", ex, HttpStatus.BAD_REQUEST, false);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + " " + error.getDefaultMessage());
        }

        ApiErrorResponse error = new ApiErrorResponse("Validation Failed", details, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return createErrorResponse("Not readable message", ex, HttpStatus.BAD_REQUEST, false);
    }

    //private methods for error handling
    private ResponseEntity<Object> createErrorResponse(String customMessage, Exception ex, HttpStatus httpStatus, boolean getRootCause) {
        String details = (getRootCause) ? ExceptionUtils.getRootCauseMessage(ex) : ex.getLocalizedMessage();
        ApiErrorResponse error = new ApiErrorResponse(customMessage, Arrays.asList(details), httpStatus.value());
        return new ResponseEntity<>(error, httpStatus);
    }

    //not needed anymore but keep just in case.
/*    private ResponseEntity<Object> createErrorResponse(String customMessage, List<String> list, HttpStatus httpStatus) {
        ApiErrorResponse error = new ApiErrorResponse(customMessage, list, httpStatus.value());
        return new ResponseEntity<>(error, httpStatus);
    }*/

}
