package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class CarNotFoundException extends WebException {
    public CarNotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND, "car-not-found");
    }
}
