package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.stream.IntStream;

public abstract class LotRepositoryBaseTest extends SpringBootApplicationTest {
    @Autowired
    protected LotRepository repository;

    @Autowired
    protected CarRepository carRepository;

    @BeforeEach
    public void setup() {
        this.cleanup();
//        this.repository.save(new Lot(1));
//        this.repository.save(new Lot(2));
//        this.repository.save(new Lot(3));
//        this.repository.save(new Lot(4));
//        this.repository.save(new Lot(5));

        IntStream
                .rangeClosed(1, 5)
                .mapToObj(Lot::new)
                .forEach(this.repository::save);
    }

    @AfterEach
    public void cleanup() {
        this.repository.deleteAll();
        this.carRepository.deleteAll();
    }

    protected void fulfillAllLots() {
        this.repository
                .findAll()
                .forEach(lot -> this.rentLotForCarByPlate(lot, this.faker.idNumber().valid()));
    }

    protected void rentLotForCar(final String carPlate) {
        var availableLots = this.repository.findByCarNull(PageRequest.ofSize(100));
        if (!availableLots.isEmpty()) {
            var lot = availableLots.get(0);
            this.rentLotForCarByPlate(lot, carPlate);
        }
    }

    private void rentLotForCarByPlate(final Lot lot, final String carPlate) {
        final Car creatingCar = new Car(
                carPlate,
                this.faker.aviation().aircraft(),
                this.faker.color().name()
        );

        final Car car = this.carRepository.save(creatingCar);

        lot.setCar(car);
        this.repository.save(lot);
    }
}
