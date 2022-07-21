package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.exception.CarConflictException;
import br.com.william.parkinglot.exception.CarNotFoundException;
import br.com.william.parkinglot.repository.CarRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.william.parkinglot.fixture.CarFixture.validCar;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarServiceTest {
    private CarService carService;
    private CarRepository carRepositoryMock;

    @BeforeAll
    public void setup() {
        this.carRepositoryMock = mock(CarRepository.class);
        this.carService = new CarService(this.carRepositoryMock);

        when(this.carRepositoryMock.findByPlate("KGK1030")).thenReturn(Optional.of(validCar("KGK1030")));
        when(this.carRepositoryMock.findByPlate("KGK1031")).thenReturn(Optional.empty());
        when(this.carRepositoryMock.findAllByPlateContainingIgnoreCaseOrColorContainingIgnoreCaseOrModelContainingIgnoreCase(
                anyString(),
                anyString(),
                anyString(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(
                List.of(validCar("KGK1030"), validCar("KGK3020")),
                PageRequest.of(0, 10),
                2
        ));
    }

    @Test
    public void shouldFindCarByPlate() {
        // given:
        var plate = "KGK1030";

        // when:
        Car car = this.carService.findByPlate(plate);

        // then:
        assertNotNull(car);
        assertEquals(plate, car.getPlate());
    }

    @Test
    public void shouldThrowCarNotFoundExceptionWhenSearchingByUnknownCar() {
        // given:
        var plate = "KGK1031";

//        // when:
//        CarNotFoundException error = null;
//        try {
//            this.carService.findByPlate(plate);
//        } catch (CarNotFoundException ex) {
//            error = ex;
//        }
//
//        // then:
//        assertNotNull(error);
//        assertEquals("car not found by plate: KGK1031", error.getMessage());

        // when:
        Executable exec = () -> this.carService.findByPlate(plate);

        // then:
        CarNotFoundException ex = assertThrows(CarNotFoundException.class, exec);
        assertEquals("car not found by plate: KGK1031", ex.getMessage());
    }

    @Test
    public void shouldFindCarByPlateWithoutCreatingANewOne() {
        // given:
        var plate = "KGK1030";

        // when:
        Car car = this.carService.findOrCreate(validCar(plate));

        // then:
        assertNotNull(car);
        assertEquals(plate, car.getPlate());
    }

    @Test
    public void shouldFindCarByPlateCreatingANewOne() {
        // given:
        var plate = "KGK1031";
        var creatingCar = validCar(plate);

        // when:
        when(this.carRepositoryMock.save(creatingCar)).thenReturn(creatingCar);
        Car car = this.carService.findOrCreate(creatingCar);

        // then:
        verify(this.carRepositoryMock, times(1)).findByPlate(plate);
        assertNotNull(car);
        assertEquals(plate, car.getPlate());
    }

    @Test
    public void shouldCreateNewCar() {
        // given:
        var plate = "KGK1031";
        var creatingCar = validCar(plate);

        // when:
        when(this.carRepositoryMock.save(creatingCar)).thenReturn(creatingCar);
        Car car = this.carService.create(creatingCar);

        // then:
        verify(this.carRepositoryMock, times(1)).save(creatingCar);
        assertNotNull(car);
        assertEquals(plate, car.getPlate());
    }

    @Test
    public void shouldNotCreateNewCarWithExistingPlate() {
        // given:
        var plate = "KGK1030";
        var creatingCar = validCar(plate);

        // when:
        when(this.carRepositoryMock.save(creatingCar))
                .thenThrow(new DataIntegrityViolationException("test"));
        Executable exec = () -> this.carService.create(creatingCar);

        // then:
        CarConflictException ex = assertThrows(CarConflictException.class, exec);
        assertEquals("Conflicted creating car with plate KGK1030", ex.getMessage());
    }

    @Test
    public void shouldPaginateAllCars() {
        // when:
        Page<Car> page = this.carService.paginateCars(1, 10, "");

        // then:
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertEquals(List.of("KGK1030", "KGK3020"), page.getContent().stream().map(Car::getPlate).collect(toList()));
    }

    @Test
    public void shouldPaginateAllCarsByNullSearchTerm() {
        // when:
        Page<Car> page = this.carService.paginateCars(1, 10, null);

        // then:
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertEquals(List.of("KGK1030", "KGK3020"), page.getContent().stream().map(Car::getPlate).collect(toList()));
    }
}