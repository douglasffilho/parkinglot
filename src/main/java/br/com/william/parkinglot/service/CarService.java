package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.exception.CarConflictException;
import br.com.william.parkinglot.exception.CarNotFoundException;
import br.com.william.parkinglot.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public Car findByPlate(final String plate) {
        return this.carRepository.findByPlate(plate)
                .orElseThrow(() -> new CarNotFoundException("car not found by plate: %s".formatted(plate)));
    }

    public Page<Car> paginateCars(final int page, final int size) {
        return this.carRepository.findAll(PageRequest.of(page - 1, size));
    }

    public Car create(Car creatingCar) {
        try {
            return this.carRepository.save(creatingCar);
        } catch (DataIntegrityViolationException ex) {
            throw new CarConflictException(
                    "Conflicted creating car with plate %s".formatted(creatingCar.getPlate()),
                    creatingCar.getPlate()
            );
        }
    }
}
