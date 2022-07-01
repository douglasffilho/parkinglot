package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class CarConflictException extends WebException {
    public CarConflictException(final String message, final String plate) {
        super(message, HttpStatus.CONFLICT, "conflict:%s".formatted(plate));
    }
}
