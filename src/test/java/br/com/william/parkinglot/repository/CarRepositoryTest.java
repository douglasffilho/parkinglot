package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.entity.Car;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;
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

    @Test
    public void shouldFindCarsByMatchingPlate() {
        // given:
        var matchingPlate = "KGK";

        // when:
        List<Car> cars = this.repository.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                matchingPlate,
                matchingPlate,
                matchingPlate,
                PageRequest.of(0, 10)
        ).getContent();

        // then
        assertEquals(2, cars.size());
        assertEquals(List.of("KGK1022", "KGK1023"), cars.stream().map(Car::getPlate).toList());
    }

    @Test
    public void shouldFindCarsByMatchingColor() {
        // given:
        var matchingColor = "prat";

        // when:
        List<Car> cars = this.repository.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                matchingColor,
                matchingColor,
                matchingColor,
                PageRequest.of(0, 10)
        ).getContent();

        // then
        assertEquals(2, cars.size());
        assertEquals(List.of("KGK1023", "KGI1025"), cars.stream().map(Car::getPlate).toList());
    }

    @Test
    public void shouldFindCarsByMatchingModel() {
        // given:
        var matchingModel = "pris";

        // when:
        List<Car> cars = this.repository.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                matchingModel,
                matchingModel,
                matchingModel,
                PageRequest.of(0, 10)
        ).getContent();

        // then
        assertEquals(2, cars.size());
        assertEquals(List.of("KGK1022", "KGK1023"), cars.stream().map(Car::getPlate).toList());
    }

    @Test
    public void shouldFindAllCarsByMatchingEmptySearch() {
        // given:
        var matchingAll = "";

        // when:
        List<Car> cars = this.repository.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                matchingAll,
                matchingAll,
                matchingAll,
                PageRequest.of(0, 10)
        ).getContent();

        // then
        assertEquals(4, cars.size());
        assertEquals(List.of("KGK1022", "KGK1023", "KGJ1024", "KGI1025"), cars.stream().map(Car::getPlate).toList());
    }

    @Test
    public void shouldNotFindAllCarsByMatchingNullSearch() {
        // given:
        String matchingAll = null;

        // when:
        List<Car> cars = this.repository.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                matchingAll,
                matchingAll,
                matchingAll,
                PageRequest.of(0, 10)
        ).getContent();

        // then
        assertEquals(0, cars.size());
    }
}