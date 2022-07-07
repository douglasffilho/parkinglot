package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.model.dto.CarDTO;
import br.com.william.parkinglot.model.mapper.CarMapper;
import br.com.william.parkinglot.service.LotService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lots")
public class LotController {
    private final LotService service;

    public LotController(final LotService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lot rentOneLot(@Valid @RequestBody CarDTO dto) {
        Car car = CarMapper.map(dto);
        return this.service.rentAvailableLot(car);
    }

    @DeleteMapping("/by-car-plate/{carPlate}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void getOutOfParking(@PathVariable(name = "carPlate") final String carPlate) {
        this.service.getOutOfParkingByCarPlate(carPlate);
    }

    @GetMapping("/by-car-plate/{carPlate}")
    public Lot findByCarPlate(@PathVariable(name = "carPlate") final String carPlate) {
        return this.service.findByCarPlate(carPlate);
    }

    @GetMapping("/{number}")
    public Lot findByNumber(@PathVariable(name = "number") final int number) {
        return this.service.findByNumber(number);
    }

    @GetMapping
    public List<Lot> findAllLots(
            @RequestParam(required = false, name = "available") final Boolean filterAvailable
    ) {
        return this.service.findAll(filterAvailable);
    }
}
