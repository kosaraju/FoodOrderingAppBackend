package com.upgrad.FoodOrderingApp.api.exception;


import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.api.model.UpdateCustomerResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException (SignUpRestrictedException exc, WebRequest request){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public  ResponseEntity<ErrorResponse> authenticationFailedException (AuthenticationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.UNAUTHORIZED);

    }


    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException (UpdateCustomerException exc,WebRequest request){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(SaveAddressException exc ,WebRequest request){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(AddressNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(CategoryNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(CouponNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(PaymentMethodNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> itemNotFoundException(ItemNotFoundException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }


}
