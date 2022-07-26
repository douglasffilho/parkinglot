package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.fixture.LotFixture;
import br.com.william.parkinglot.model.dto.CarDTO;
import br.com.william.parkinglot.service.LotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LotControllerTest extends SpringBootApplicationTest {
    @MockBean
    LotService lotService;

    // rentOneLot - caminho feliz = carro valido, não esta ja estacionado e vaga disponivel
    @Test
    public void shouldParkCar() throws Exception {
        // given
        CarDTO dto = new CarDTO();
        dto.plate = "KGK1020";
        dto.model = "Prisma";
        dto.color = "Preto";
        Lot lot = LotFixture.validOccupiedLot(1, "KGK1020");

        // when
        when(this.lotService.rentAvailableLot(any(Car.class))).thenReturn(lot);
        var actions = this.mockMvc
                .perform(
                        post("/lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsBytes(dto))
                );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(lot)));
    }

    // rentOneLot - caminho infeliz = carro valido, não esta estacionado, mas, não tem vaga disponivel
    @Test
    public void shouldNotParkCarWhenThereAreNotAvailableLots() throws Exception {
        // given
        CarDTO dto = new CarDTO();
        dto.plate = "KGK1020";
        dto.model = "Prisma";
        dto.color = "Preto";

        // when
        when(this.lotService.rentAvailableLot(any(Car.class))).thenThrow(new AvailableLotNotFoundException());
        var actions = this.mockMvc
                .perform(
                        post("/lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsBytes(dto))
                );

        // then
        actions
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            "message": "Não há mais vagas disponiveis!",
                            "status": 404,
                            "logref": "lot-not-found"
                        }
                        """));
    }

    // rentOneLot - caminho infeliz = carro valido, mas, já esta estacionado
    @Test
    public void shouldNotParkCarWhenCarAlreadyParked() throws Exception {
        // given
        CarDTO dto = new CarDTO();
        dto.plate = "KGK1020";
        dto.model = "Prisma";
        dto.color = "Preto";

        // when
        when(this.lotService.rentAvailableLot(any(Car.class))).thenThrow(new CarAlreadyParkedException("teste"));
        var actions = this.mockMvc
                .perform(
                        post("/lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsBytes(dto))
                );

        // then
        actions
                .andExpect(status().isConflict())
                .andExpect(content().json("""
                        {
                            "message": "teste",
                            "status": 409,
                            "logref": "conflict-lot"
                        }
                        """));
    }

    // rentOneLot - caminho infeliz = carro invalido (DTO)
    @Test
    public void shouldNotParkCarWhenCarIsInvalid() throws Exception {
        // given
        CarDTO dto = new CarDTO();
        dto.plate = "KGK102";
        dto.model = "Prisma Nome Muito Grande";
        dto.color = "Preto";

        // when
        var actions = this.mockMvc
                .perform(
                        post("/lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsBytes(dto))
                );

        // then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "message": "model:invalid-model,plate:invalid-plate",
                            "status": 400,
                            "logref": "bad-request"
                        }
                        """));
    }

    // rentOneLot - caminho infeliz = carro com dados faltantes (DTO)
    @Test
    public void shouldNotParkCarWhenCarDataIsMissing() throws Exception {
        // given
        CarDTO dto = new CarDTO();

        // when
        var actions = this.mockMvc
                .perform(
                        post("/lots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsBytes(dto))
                );

        // then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "message": "color:missing-color,model:missing-model,plate:missing-plate",
                            "status": 400,
                            "logref": "bad-request"
                        }
                        """));
    }

}