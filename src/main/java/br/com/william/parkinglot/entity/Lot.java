package br.com.william.parkinglot.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "lots")
public class Lot {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private int number;

    @OneToOne
    @JoinColumn(name = "car_id", unique = true)
    private Car car;

    public Lot() {
        this.id = UUID.randomUUID().toString();
    }

    public Lot(final Integer number) {
        this();
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
