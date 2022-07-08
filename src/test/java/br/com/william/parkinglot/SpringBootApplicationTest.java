package br.com.william.parkinglot;

import com.github.javafaker.Faker;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SpringBootApplicationTest {
    protected Faker faker = new Faker();
}
