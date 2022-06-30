package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, String> {
    Optional<Car> findByPlate(final String plate);
}
