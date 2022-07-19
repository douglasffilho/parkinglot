package br.com.william.parkinglot;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SpringBootApplicationTest {
    protected Faker faker = new Faker();

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
}
