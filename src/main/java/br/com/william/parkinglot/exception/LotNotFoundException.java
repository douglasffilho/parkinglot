package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

public class LotNotFoundException extends WebException {
    public LotNotFoundException(final String carPlate) {
        super(
                "Carro não está estacionado: %s".formatted(carPlate),
                HttpStatus.NOT_FOUND,
                "lot-not-found"
        );
    }

    public LotNotFoundException(final int number) {
        super(
                "Vaga não existe: %s".formatted(number),
                HttpStatus.NOT_FOUND,
                "lot-not-found"
        );
    }
}
