package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Lot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, String> {
    List<Lot> findByCarNull(Pageable page);

    Optional<Lot> findByCarPlate(String carPlate);

    Optional<Lot> findByNumber(int number);

    List<Lot> findAllByCarNull();

    // @Query(value = "SELECT * FROM lots WHERE car_id not null")
    List<Lot> findAllByCarNotNull();
}
