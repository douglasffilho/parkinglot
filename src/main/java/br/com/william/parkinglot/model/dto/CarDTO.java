package br.com.william.parkinglot.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CarDTO {
    @NotBlank(message = "missing-plate")
    @Size(min = 7, max = 7, message = "invalid-plate")
    public String plate;

    @NotBlank(message = "missing-model")
    @Size(max = 30, message = "invalid-model")
    public String model;

    @NotBlank(message = "missing-color")
    public String color;
}
