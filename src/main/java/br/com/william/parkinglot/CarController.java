package br.com.william.parkinglot;

import br.com.william.parkinglot.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {
    @Autowired
    private CarService carService;

    @GetMapping("/{plate}")// http://localhost:8080/cars/PDU1234
    public Car findByPlate(@PathVariable(name = "plate") final String plate) {
        return this.carService.findByPlate(plate);
    }
}
