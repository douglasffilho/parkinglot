package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Lot;
import org.junit.jupiter.api.Test;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Test
    public void shouldFindLotByNumber() {
        // given:
        var number = 1;

        // when:
        Optional<Lot> lotWrapper = this.repository.findByNumber(number);

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isPresent());
        Lot lot = lotWrapper.get();
        assertEquals(number, lot.getNumber());
    }

    @Test
    public void shouldNotFindLotByNumber() {
        // given:
        var number = 10;

        // when:
        Optional<Lot> lotWrapper = this.repository.findByNumber(number);

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isEmpty());
    }

    @Test
    public void shouldFindAllAvailableLots() {
        // given:
        this.rentLotForCar("KGK1030");

        // when:
        List<Lot> availableLots = this.repository.findAllByCarNull();

        // then:
        assertNotNull(availableLots);
        assertEquals(4, availableLots.size());
        assertEquals(List.of(2, 3, 4, 5), availableLots.stream().map(Lot::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void shouldFindNoAvailableLots() {
        // given:
        this.fulfillAllLots();

        // when:
        List<Lot> availableLots = this.repository.findAllByCarNull();

        // then:
        assertNotNull(availableLots);
        assertTrue(availableLots.isEmpty());
    }

    @Test
    public void shouldFindAllOccupiedLots() {
        // given:
        var carPlate = "KGK1030";
        this.rentLotForCar(carPlate);

        // when:
        List<Lot> occupiedLots = this.repository.findAllByCarNotNull();

        // then:
        assertNotNull(occupiedLots);
        assertEquals(1, occupiedLots.size());
        assertEquals(carPlate, occupiedLots.get(0).getCar().getPlate());
    }

    @Test
    public void shouldFindOnlyAvailableLots() {
        // when:
        List<Lot> occupiedLots = this.repository.findAllByCarNotNull();

        // then:
        assertNotNull(occupiedLots);
        assertTrue(occupiedLots.isEmpty());
    }
}