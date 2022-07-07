package br.com.william.parkinglot.repository;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Lot;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class LotRepositoryTest extends LotRepositoryBaseTest {

    @Test
    public void shouldFindAnAvailableLot() {
        // when:
        Optional<Lot> lotWrapper = this.repository.findFirstByCarNull();

        // then:
        assertNotNull(lotWrapper);
        assertTrue(lotWrapper.isPresent());
        Lot lot = lotWrapper.get();
        assertNull(lot.getCar());
        assertEquals(1, lot.getNumber());
    }

}