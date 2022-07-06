package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.service.LotService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lots")
public class LotController {
    private final LotService service;

    public LotController(final LotService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lot rentOneLot(@RequestBody Car car) {
        return this.service.rentAvailableLot(car);
    }

    @DeleteMapping("/{carPlate}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getOutOfParking(@PathVariable(name = "carPlate") final String carPlate) {
        this.service.getOutOfParkingByCarPlate(carPlate);
    }
}
