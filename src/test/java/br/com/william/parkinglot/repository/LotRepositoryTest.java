package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Lot;
import org.junit.jupiter.api.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LotRepositoryTest extends LotRepositoryBaseTest {
    @Test
    public void shouldFindAnAvailableLot() {
        // when:
        Optional<Lot> lotWrapper = this.repository.findFirstByCarNull();

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isPresent());
        Lot lot = lotWrapper.get();
        assertNull(lot.getCar());
        assertEquals(1, lot.getNumber());
    }

    @Test
    public void shouldNotFindAnAvailableLot() {
        // given:
        this.fulfillAllLots();

        // when:
        Optional<Lot> lotWrapper = this.repository.findFirstByCarNull();

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isEmpty());
    }

    @Test
    public void shouldFindLotByCarPlate() {
        // given:
        var carPlate = "KGK1020";
        this.rentLotForCar(carPlate);

        // when:
        Optional<Lot> lotWrapper = this.repository.findByCarPlate(carPlate);

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isPresent());
        Lot lot = lotWrapper.get();
        assertNotNull(lot.getCar());
        assertEquals(carPlate, lot.getCar().getPlate());
    }

    @Test
    public void shouldNotFindLotByCarPlate() {
        // given:
        var carPlate = "KGK1022";

        // when:
        Optional<Lot> lotWrapper = this.repository.findByCarPlate(carPlate);

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isEmpty());
    }

    @Test
    public void shouldThrowErrorWhenSearchingLotByNullCarPlate() {
        // given:
        String carPlate = null;

        // when:
        Throwable error = null;
        try {
            this.repository.findByCarPlate(carPlate);
        } catch (Exception ex) {
            error = ex;
        }

        // then:
        assertNotNull(error);
        assertTrue(error instanceof IncorrectResultSizeDataAccessException);
    }
}