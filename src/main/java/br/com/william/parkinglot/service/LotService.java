package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.BadRequestException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.exception.LotNotFoundException;
import br.com.william.parkinglot.repository.LotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LotService {
    private final LotRepository repository;
    private final CarService carService;

    public LotService(
            final LotRepository repository,
            final CarService carService
    ) {
        this.repository = repository;
        this.carService = carService;
    }

    public Lot rentAvailableLot(Car car) {
        if (car == null) throw new BadRequestException("Objeto carro invalido", "invalid-car-object");

        this.repository.findByCarPlate(car.getPlate()).ifPresent(lot -> {
            throw new CarAlreadyParkedException(
                    "O carro já está estacionado na vaga: %s".formatted(lot.getNumber())
            );
        });

        Lot lot = this.repository
                .findFirstByCarNull()
                .orElseThrow(AvailableLotNotFoundException::new);

        Car registeredCar = this.carService.findOrCreate(car);

        lot.setCar(registeredCar);

        return this.repository.save(lot);
    }

    public void getOutOfParkingByCarPlate(final String carPlate) {
        this.repository
                .findByCarPlate(carPlate)
                .ifPresent(lot -> {
                    lot.setCar(null);
                    this.repository.save(lot);
                });
    }

    public Lot findByCarPlate(final String carPlate) {
        return this.repository
                .findByCarPlate(carPlate)
                .orElseThrow(() -> new LotNotFoundException(carPlate));
    }

    public Lot findByNumber(final int number) {
        return this.repository
                .findByNumber(number)
                .orElseThrow(() -> new LotNotFoundException(number));
    }

    public List<Lot> findAll(final Boolean filterAvailable) {
        if (filterAvailable == null)
            return this.repository.findAll();

        if (filterAvailable)
            return this.repository.findAllByCarNull();

        return this.repository.findAllByCarNotNull();
    }
}
