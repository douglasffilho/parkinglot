package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WebException {

    public BadRequestException(final String message, final String logref) {
        super(message, HttpStatus.BAD_REQUEST, logref);
    }
}
