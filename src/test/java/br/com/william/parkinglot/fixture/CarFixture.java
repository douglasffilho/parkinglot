package br.com.william.parkinglot.fixture;

import br.com.william.parkinglot.entity.Car;

public class CarFixture {
    private CarFixture() {
    }

    public static Car validCar(final String plate) {
        return new Car(
                plate,
                "Prisma",
                "Preto"
        );
    }
}
