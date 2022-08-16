package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.BadRequestException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.exception.LotNotFoundException;
import br.com.william.parkinglot.repository.LotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Optional;

import static br.com.william.parkinglot.fixture.CarFixture.validCar;
import static br.com.william.parkinglot.fixture.LotFixture.validAvailableLot;
import static br.com.william.parkinglot.fixture.LotFixture.validOccupiedLot;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LotServiceTest {
    private LotRepository repositoryMock;
    private CarService carServiceMock;
    private LotService service;

    @BeforeEach
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
    @Test
    public void shouldGetOutParkingByCarPlate() {
        // given:
        Lot occupiedLot = validOccupiedLot(1, "KGK1020");

        // when:
        when(this.repositoryMock.findByCarPlate(occupiedLot.getCar().getPlate())).thenReturn(Optional.of(occupiedLot));
        this.service.getOutOfParkingByCarPlate(occupiedLot.getCar().getPlate());

        // then:
        verify(this.repositoryMock, times(1)).save(occupiedLot);
    }

    //getOutOfParkingByCarPlate não tem vagas ocupadas pelo carro, 0 invocaçoes do metodo save
    @Test
    public void shouldNotGetOutParkingByCarNotParked() {
        // given:
        var carPlate = "KGK3020";

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate)).thenReturn(Optional.empty());
        this.service.getOutOfParkingByCarPlate(carPlate);

        // then:
        verify(this.repositoryMock, times(0)).save(any(Lot.class));
    }

    //findByCarPlate com carro estacionado
    @Test
    public void shouldFindLotByCarPlate() {
        // given:
        Lot occupiedLot = validOccupiedLot(1, "KGK1020");

        // when:
        when(this.repositoryMock.findByCarPlate(occupiedLot.getCar().getPlate())).thenReturn(Optional.of(occupiedLot));
        Lot lot = this.service.findByCarPlate(occupiedLot.getCar().getPlate());

        // then:
        assertNotNull(lot);
        assertEquals(occupiedLot.getCar().getPlate(), lot.getCar().getPlate());
    }

    //findByCarPlate com placa nula, lanca erro IncorrectResultSizeDataAccessException
    @Test
    public void shouldThrowExceptionByNullCarPlate() {
        // given:
        String carPlate = null;

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate))
                .thenThrow(new IncorrectResultSizeDataAccessException("not only one value was found", 1, 5));
        Executable exec = () -> this.service.findByCarPlate(carPlate);

        // then:
        IncorrectResultSizeDataAccessException ex = assertThrows(IncorrectResultSizeDataAccessException.class, exec);
        assertEquals("not only one value was found", ex.getMessage());
    }

    //findByCarPlate sem carro estacionado LotNotFoundException
    @Test
    public void shouldThrowLotNotFoundExceptionByUnknownCarPlate() {
        // given:
        String carPlate = "KGK3020";

        // when:
        when(this.repositoryMock.findByCarPlate(carPlate)).thenReturn(Optional.empty());
        Executable exec = () -> this.service.findByCarPlate(carPlate);

        // then:
        LotNotFoundException ex = assertThrows(LotNotFoundException.class, exec);
        assertEquals("Carro não está estacionado: KGK3020", ex.getMessage());
    }

    //findByNumber encontra pelo numero
    @Test
    public void shouldFindLotByNumber() {
        // given:
        Lot availableLot = validAvailableLot(1);

        // when:
        when(this.repositoryMock.findByNumber(availableLot.getNumber())).thenReturn(Optional.of(availableLot));
        Lot lot = this.service.findByNumber(availableLot.getNumber());

        // then:
        assertNotNull(lot);
        assertEquals(availableLot.getNumber(), lot.getNumber());
    }

    //findByNumber nao encontra pelo numero LotNotFoundException
    @Test
    public void shouldThrowLotNotFoundExceptionByUnknownNumber() {
        // given:
        int number = 20;

        // when:
        when(this.repositoryMock.findByNumber(number)).thenReturn(Optional.empty());
        Executable exec = () -> this.service.findByNumber(number);

        // then:
        LotNotFoundException ex = assertThrows(LotNotFoundException.class, exec);
        assertEquals("Vaga não existe: 20", ex.getMessage());
    }


    //findAll filtro nulo (validar invocacao de repository.findAll())
    @Test
    public void shouldFindAllLotsWithoutFiltering() {
        // given:
        Boolean filter = null;

        // when:
        this.service.findAll(filter);

        // then:
        verify(this.repositoryMock, times(1)).findAll();
        verify(this.repositoryMock, times(0)).findAllByCarNotNull();
        verify(this.repositoryMock, times(0)).findAllByCarNull();
    }

    //findAll filtro false (validar invocacao de repository.findAllByCarNotNull())
    @Test
    public void shouldFindAllLotsFilteringByAvailable() {
        // given:
        Boolean filter = true;

        // when:
        this.service.findAll(filter);

        // then:
        verify(this.repositoryMock, times(0)).findAll();
        verify(this.repositoryMock, times(0)).findAllByCarNotNull();
        verify(this.repositoryMock, times(1)).findAllByCarNull();
    }

    //findAll filtro true (validar invocacao de repository.findAllByCarNull())
    @Test
    public void shouldFindAllLotsFilteringByOccupied() {
        // given:
        Boolean filter = false;

        // when:
        this.service.findAll(filter);

        // then:
        verify(this.repositoryMock, times(0)).findAll();
        verify(this.repositoryMock, times(1)).findAllByCarNotNull();
        verify(this.repositoryMock, times(0)).findAllByCarNull();
    }

    // [changeFromLotToLotByNumber] - feliz - encontra a vaga pelo numero
    // [changeFromLotToLotByNumber] - triste - não encontra a vaga pelo numero (LotNotFoundException)
    // [changeFromLotToLotByNumber] - triste - não tem carro na vaga atual (CarNotFoundException)
    // [changeFromLotToLotByNumber] - triste - tem carro na vaga atual, mas, placas diferentes (InvalidCarMoveException)
    // [changeFromLotToLotByNumber] - triste - não encontra a proxima vaga por numero (LotNotFoundException)
    // [changeFromLotToLotByNumber] - triste - já existe um carro na vaga de destino (CarAlreadyParkedException)
    // [changeFromLotToLotByNumber] - feliz - remove carro da vaga atual e coloca na proxima vaga

}