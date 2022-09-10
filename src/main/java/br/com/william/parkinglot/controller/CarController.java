package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.model.dto.CarDTO;
import br.com.william.parkinglot.model.mapper.CarMapper;
import br.com.william.parkinglot.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin("*")
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(final CarService carService) {
        this.carService = carService;
    }

    /**
     * GET http://localhost:8080/cars/PDU1234
     * retorna status 200 (OK) quando consegue encontrar
     * retorna status 404 (NOT_FOUND) quando não consegue encontrar
     */
    @GetMapping("/{plate}")
    public Car findByPlate(@PathVariable(name = "plate") final String plate) {
        return this.carService.findByPlate(plate);
    }

    /**
     * GET http://localhost:8080/cars?page=1&size=10&filter=pret
     * retorna status 200 (OK) com resultado da listagem (Page)
     */
    @GetMapping
    public Page<Car> findAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        return this.carService.paginateCars(page, size, filter);
    }

    /**
     * POST http://localhost:8080/cars
     * corpo da requisição:
     * {"plate": "Placa", "model": "modelo", "color": "cor"}
     * <p>
     * retorna status 201 (CREATED) quando for criado o recurso
     * retorna status 400 (BAD_REQUEST) caso falte alguma informação
     * retorna status 409 (CONFLICT) caso ja existe um recurso com mesma placa
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car create(@Valid @RequestBody CarDTO dto) {
        Car creatingCar = CarMapper.map(dto);
        return this.carService.create(creatingCar);
    }

    @DeleteMapping("/{carPlate}")
    @ResponseStatus(HttpStatus.OK)
    public Car delete(@PathVariable("carPlate") String carPlate) {
        return this.carService.deleteByPlate(carPlate);
    }
}
