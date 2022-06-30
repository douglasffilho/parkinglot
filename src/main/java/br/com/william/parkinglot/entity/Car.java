package br.com.william.parkinglot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String plate;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    public Car() {}

    public Car(final String id, final String plate, final String model, final String color) {
        this.id = id;
        this.plate = plate;
        this.model = model;
        this.color = color;
    }

    public Car(final String plate, final String model, final String color) {
        this(UUID.randomUUID().toString(), plate, model, color);
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPlate() {
        return this.plate;
    }

    public void setPlate(final String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(final String color) {
        this.color = color;
    }
}
