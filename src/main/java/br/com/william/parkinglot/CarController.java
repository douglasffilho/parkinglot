package br.com.william.parkinglot;

import br.com.william.parkinglot.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/cars")
public class CarController {
    @Autowired
    private CarService carService;

    /**
     * GET http://localhost:8080/cars/PDU1234
     * retorna status 200 (OK) quando consegue encontrar
     * retorn status 404 (NOT_FOUND) quando não consegue encontrar
     */
    @GetMapping("/{plate}")
    public Car findByPlate(@PathVariable(name = "plate") final String plate) {
        return this.carService.findByPlate(plate);
    }

    /**
     * GET http://localhost:8080/cars?page=1&size=10
     * retorna status 200 (OK) com resultado da listagem (Page)
     */
    @GetMapping
    public Page<Car> findAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        return this.carService.paginateCars(page, size);
    }

    /**
     * POST http://localhost:8080/cars
     * corpo da requisição:
     * {"plate": "Placa", "model": "modelo", "color": "cor"}
     *
     * retorna status 201 (CREATED) quando for criado o recurso
     * retorna status 400 (BAD_REQUEST) caso falte alguma informação
     * retorna status 409 (CONFLICT) caso ja existe um recurso com mesma placa
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car create(@RequestBody Car creatingCar) {
        return this.carService.create(creatingCar);
    }
}
