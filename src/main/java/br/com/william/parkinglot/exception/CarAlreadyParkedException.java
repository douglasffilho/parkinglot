package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class CarAlreadyParkedException extends WebException {
    public CarAlreadyParkedException(final String message) {
        super(message, HttpStatus.CONFLICT, "conflict-lot");
    }
}
