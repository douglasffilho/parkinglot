package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.*;
import br.com.william.parkinglot.repository.LotRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

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
                    format("O carro já está estacionado na vaga: %s", lot.getNumber())
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

    public Lot changeFromLotToLotByNumber(final String carPlate, final int currentLotNumber, final int nextLotNumber) {
        if (!StringUtils.hasText(carPlate))
            throw new BadRequestException("Informe uma placa válida", "invalid-car-plate");

        Lot current = this.findByNumber(currentLotNumber);

        Optional
                .ofNullable(current.getCar())
                .map(Car::getPlate)
                .ifPresentOrElse(
                        (plate) -> {
                            if (!plate.equals(carPlate))
                                throw new InvalidCarMoveException(carPlate);
                        },
                        () -> {
                            throw new CarNotFoundException(
                                    String.format("carro de placa %s não encontrado para vaga %s", carPlate, currentLotNumber)
                            );
                        }
                );

        Lot next = findByNumber(nextLotNumber);
//        if (next.getCar() != null && next.getCar().getPlate() != null) {
//            throw new CarConflictException("Já existe um carro estacionado nesta vaga", next.getCar().getPlate());
//        }
        Optional
                .ofNullable(next.getCar())
                .ifPresent(car -> {
                    throw new CarAlreadyParkedException(
                            String.format("Já existe um carro estacionado nesta vaga: %s", car.getPlate())
                    );
                });
        next.setCar(current.getCar());
        current.setCar(null);

        this.repository.save(current);
        return this.repository.save(next);
    }
}
