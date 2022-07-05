package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, String> {
    // SELECT * FROM lots WHERE car_id=NULL LIMIT 1;
    Optional<Lot> findFirstByCarNull();

    Optional<Lot> findByCarPlate(String carPlate);
}
