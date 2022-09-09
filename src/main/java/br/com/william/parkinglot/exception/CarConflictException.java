package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

import static java.lang.String.format;

public class CarConflictException extends WebException {
    public CarConflictException(final String message, final String plate) {
        super(message, HttpStatus.CONFLICT, format("conflict-car:%s", plate));
    }
}
