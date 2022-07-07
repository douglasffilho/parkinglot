package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Car;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRepositoryTest extends CarRepositoryBaseTest {

    @Test
    public void shouldFindCarByPlate() {
        // given:
        var plate = "KGK1022";

        // when:
        Optional<Car> carWrapper = this.repository.findByPlate(plate);

        // then:
        assertNotNull(carWrapper);
        //assertFalse(carWrapper.isEmpty());
        assertTrue(carWrapper.isPresent());
        Car car = carWrapper.get();
        assertEquals(plate, car.getPlate());
        assertEquals("Prisma", car.getModel());
        assertEquals("Preto", car.getColor());
    }

    @Test
    public void shouldNotFindCarByPlate() {
        // given:
        var plate = "kGK1010";

        // when:
        Optional<Car> carWrapper = this.repository.findByPlate(plate);

        // then:
        assertNotNull(carWrapper);
        //assertFalse(carWrapper.isPresent());
        assertTrue(carWrapper.isEmpty());
    }

    @Test
    public void shouldNotFindCarByNullPlate() {
        // given:
        String plate = null;

        // when:
        Optional<Car> carWrapper = this.repository.findByPlate(plate);

        // then:
        assertNotNull(carWrapper);
        //assertFalse(carWrapper.isPresent());
        assertTrue(carWrapper.isEmpty());
    }
}