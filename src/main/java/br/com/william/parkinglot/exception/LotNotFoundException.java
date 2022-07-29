package br.com.william.parkinglot.exception;

import org.springframework.http.HttpStatus;

import static java.lang.String.format;

public class LotNotFoundException extends WebException {
    public LotNotFoundException(final String carPlate) {
        super(
                format("Carro não está estacionado: %s", carPlate),
                HttpStatus.NOT_FOUND,
                "lot-not-found"
        );
    }

    public LotNotFoundException(final int number) {
        super(
                format("Vaga não existe: %s", number),
                HttpStatus.NOT_FOUND,
                "lot-not-found"
        );
    }
}
