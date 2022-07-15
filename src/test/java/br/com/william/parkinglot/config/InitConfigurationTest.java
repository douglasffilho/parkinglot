package br.com.william.parkinglot.config;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.repository.LotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@ActiveProfiles({"test", "init"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InitConfigurationTest extends SpringBootApplicationTest {
    @Autowired
    LotRepository repository;

    List<String> availableLotsIds;

    @Test
    public void shouldRegisterNewLotsOnNewDatabase() {
        // when:
        var totalAvailableLots = this.repository.findAll();
        this.availableLotsIds = totalAvailableLots
                .stream()
                .map(Lot::getId)
                .toList();

        // then:
        assertEquals(5, totalAvailableLots.size());
    }

    @Test
    public void shouldNotRegisterNewLotsOnAlreadyConfiguredDatabase() {
        // when:
        var totalAvailableLots = this.repository.findAll();
        var lotsIds = totalAvailableLots
                .stream()
                .map(Lot::getId)
                .toList();

        // then:
        assertEquals(5, totalAvailableLots.size());
        assertEquals(this.availableLotsIds, lotsIds);
    }

}