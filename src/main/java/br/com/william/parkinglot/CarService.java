package br.com.william.parkinglot;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public Car findByPlate(final String plate) {
        return this.carRepository.findByPlate(plate)
                .orElseThrow(() -> new RuntimeException("car not found by plate: %s".formatted(plate)));
    }
}
