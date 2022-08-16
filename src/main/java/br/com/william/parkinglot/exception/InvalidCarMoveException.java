package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class InvalidCarMoveException extends WebException {

    public InvalidCarMoveException(final String requestCarPlate) {
        super(
                String.format(
                        "A vaga não está ocupada pelo carro de placa %s",
                        requestCarPlate
                ),
                HttpStatus.BAD_REQUEST,
                "invalid-car-move"
        );
    }
}
