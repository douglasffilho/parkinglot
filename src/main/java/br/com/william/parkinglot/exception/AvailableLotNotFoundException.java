package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class AvailableLotNotFoundException extends WebException {
    public AvailableLotNotFoundException() {
        super("Não há mais vagas disponiveis!", HttpStatus.NOT_FOUND, "lot-not-found");
    }
}
