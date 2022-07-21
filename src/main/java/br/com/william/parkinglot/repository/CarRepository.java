package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, String> {
    Optional<Car> findByPlate(final String plate);

    Page<Car> findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
            String plate,
            String model,
            String color,
            Pageable pageable
    );
}
