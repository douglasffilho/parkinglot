package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.BadRequestException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.repository.LotRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Optional;

import static br.com.william.parkinglot.fixture.CarFixture.validCar;
import static br.com.william.parkinglot.fixture.LotFixture.validAvailableLot;
import static br.com.william.parkinglot.fixture.LotFixture.validOccupiedLot;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LotServiceTest {
    private LotRepository repositoryMock;
    private CarService carServiceMock;
    private LotService service;

    @BeforeAll
    public void setup() {
        this.repositoryMock = mock(LotRepository.class);
        this.carServiceMock = mock(CarService.class);
        this.service = new LotService(this.repositoryMock, this.carServiceMock);
    }

    //rentAvailableLot sem o carro ja estar estacionado, com vaga disponivel, com carro ja cadastrado
    @Test
    public void shouldParkCarOnAvailableLot() {
        // given:
        var carPlate = "KGK1020";
        Car car = validCar(carPlate);

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate)).thenReturn(Optional.empty());
        when(this.repositoryMock.findFirstByCarNull()).thenReturn(Optional.of(validAvailableLot(1)));
        when(this.carServiceMock.findOrCreate(car)).thenReturn(car);
        when(this.repositoryMock.save(any(Lot.class))).thenAnswer((lot) -> lot.getArgument(0));
        Lot occupiedLot = this.service.rentAvailableLot(car);

        // then:
        assertNotNull(occupiedLot);
        assertEquals(1, occupiedLot.getNumber());
        assertEquals(carPlate, occupiedLot.getCar().getPlate());
    }

    //rentAvailableLot sem o carro ja estar estacionado, com vaga indisponivel (AvailableLotNotFoundException)
    @Test
    public void shouldNotParkCarOnNotAvailableLot() {
        // given:
        var carPlate = "KGK1020";
        Car car = validCar(carPlate);

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate)).thenReturn(Optional.empty());
        when(this.repositoryMock.findFirstByCarNull()).thenReturn(Optional.empty());
        Executable exec = () -> this.service.rentAvailableLot(car);

        // then:
        AvailableLotNotFoundException ex = assertThrows(AvailableLotNotFoundException.class, exec);
        assertNotNull(ex);
        assertEquals("Não há mais vagas disponiveis!", ex.getMessage());
    }

    //rentAvailableLot com o carro ja estacionado CarAlreadyParkedException
    @Test
    public void shouldNotParkAlreadyParkedCar() {
        // given:
        var carPlate = "KGK1020";
        Car car = validCar(carPlate);

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate)).thenReturn(Optional.of(validOccupiedLot(3, carPlate)));
        Executable exec = () -> this.service.rentAvailableLot(car);

        // then:
        CarAlreadyParkedException ex = assertThrows(CarAlreadyParkedException.class, exec);
        assertNotNull(ex);
        assertEquals("O carro já está estacionado na vaga: 3", ex.getMessage());
    }

    //rentAvailableLot com o carro sem placa IncorrectResultSizeDataAccessException
    @Test
    public void shouldNotParkInvalidPlateCar() {
        // given:
        Car car = validCar(null);

        // when:
        when(this.repositoryMock.findByCarPlate(car.getPlate()))
                .thenThrow(new IncorrectResultSizeDataAccessException("not only one value was found", 1, 5));
        Executable exec = () -> this.service.rentAvailableLot(car);

        // then:
        IncorrectResultSizeDataAccessException ex = assertThrows(IncorrectResultSizeDataAccessException.class, exec);
        assertNotNull(ex);
        assertEquals("not only one value was found", ex.getMessage());
    }

    //rentAvailableLot com o carro null BadRequestException
    @Test
    public void shouldNotParkInvalidCar() {
        // given:
        Car car = null;

        // when:
        Executable exec = () -> this.service.rentAvailableLot(car);

        // then:
        BadRequestException ex = assertThrows(BadRequestException.class, exec);
        assertNotNull(ex);
        assertEquals("Objeto carro invalido", ex.getMessage());
    }

    //getOutOfParkingByCarPlate encontra vaga ocupada pelo carro, remove o carro da vaga (1 invocacao do metodo save)
    //getOutOfParkingByCarPlate não tem vagas ocupadas pelo carro, 0 invocaçoes do metodo save
}