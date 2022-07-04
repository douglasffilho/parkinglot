package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotRepository extends JpaRepository<Lot, String> {
}
