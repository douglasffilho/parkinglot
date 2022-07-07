package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Lot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

public abstract class LotRepositoryBaseTest extends SpringBootApplicationTest {
    @Autowired
    protected LotRepository repository;

    @BeforeEach
    public void setup() {
        this.repository.deleteAll();
//        this.repository.save(new Lot(1));
//        this.repository.save(new Lot(2));
//        this.repository.save(new Lot(3));
//        this.repository.save(new Lot(4));
//        this.repository.save(new Lot(5));

        IntStream
                .rangeClosed(1, 5)
                .mapToObj(Lot::new)
                .forEach(this.repository::save);
    }

    @AfterEach
    public void cleanup() {
        this.repository.deleteAll();
    }
}
