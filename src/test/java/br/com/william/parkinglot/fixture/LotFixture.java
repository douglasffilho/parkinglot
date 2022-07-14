package br.com.william.parkinglot.fixture;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;

import static br.com.william.parkinglot.fixture.CarFixture.validCar;

public class LotFixture {
    private LotFixture() {
    }

    public static Lot validAvailableLot(final int number) {
        return new Lot(number);
    }

    public static Lot validOccupiedLot(final int number, final String carPlate) {
        Lot lot = validAvailableLot(number);
        Car car = validCar(carPlate);

        lot.setCar(car);

        return lot;
    }
}
