package br.com.william.parkinglot.model.mapper;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.model.dto.CarDTO;

public class CarMapper {
    private CarMapper() {}

    public static Car map(final CarDTO dto) {
        return new Car(dto.plate, dto.model, dto.color);
    }
}
