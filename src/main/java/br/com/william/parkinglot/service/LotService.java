package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.repository.LotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@Service
public class LotService {
    private static final Logger log = LoggerFactory.getLogger(LotService.class);

    private static final int LOTS_AVAILABLE = 5;

    private final LotRepository repository;

    private final CarService carService;

    public LotService(
            final LotRepository repository,
            final CarService carService
    ) {
        this.repository = repository;
        this.carService = carService;
    }

    @PostConstruct
    public void init() {
        log.info("INICIANDO VALIDAÇÃO DE VAGAS");
        final long contagemDeVagasNoBanco = this.repository.count();
        if (contagemDeVagasNoBanco != LOTS_AVAILABLE) {
            log.info("CRIANDO VAGAS NO BANCO DE DADOS");
            this.repository.deleteAll();
            IntStream
                    .rangeClosed(1, LOTS_AVAILABLE)
                    .mapToObj(Lot::new)
                    .forEach(this.repository::save);

//            IntStream
//                    .rangeClosed(1, LOTS_AVAILABLE)
//                    .mapToObj(number -> new Lot(number))
//                    .forEach(lot -> this.repository.save(lot));

//            for (int i = 1; i <= LOTS_AVAILABLE; i++) {
//                Lot newLot = new Lot(i);
//
//                this.repository.save(newLot);
//            }
        }
    }

    public Lot rentAvailableLot(Car car) {
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
}
