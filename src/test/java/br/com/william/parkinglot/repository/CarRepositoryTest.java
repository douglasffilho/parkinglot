package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarRepositoryTest extends SpringBootApplicationTest {
    @Autowired
    CarRepository repository;

    @BeforeAll
    public void setup() {
        this.repository.save(new Car(
                "KGK1022",
                "Prisma",
                "Preto"
        ));

        this.repository.save(new Car(
                "KGK1023",
                "Prisma",
                "Prata"
        ));
    }

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

    @AfterAll
    public void cleanup() {
        this.repository.deleteAll();
    }
}