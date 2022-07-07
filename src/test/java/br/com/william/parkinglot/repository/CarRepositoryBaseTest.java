package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CarRepositoryBaseTest extends SpringBootApplicationTest {
    @Autowired
    protected CarRepository repository;

    @BeforeAll
    public void setup() {
        this.repository.save(new Car(
                "KGK1022",
                "Prisma",
                "Preto"
        ));

        this.repository.save(new Car(
                "KGK1023",
                "Prisma",
                "Prata"
        ));
    }

    @AfterAll
    public void cleanup() {
        this.repository.deleteAll();
    }
}
