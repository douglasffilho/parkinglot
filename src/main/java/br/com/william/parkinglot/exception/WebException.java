package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public abstract class WebException extends RuntimeException {
    private final HttpStatus status;
    private final String logref;

    public WebException(final String message, final HttpStatus status, final String logref) {
        super(message);
        this.status = status;
        this.logref = logref;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getLogref() {
        return this.logref;
    }
}
