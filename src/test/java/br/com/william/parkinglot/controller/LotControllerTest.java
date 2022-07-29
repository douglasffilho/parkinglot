package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.exception.AvailableLotNotFoundException;
import br.com.william.parkinglot.exception.CarAlreadyParkedException;
import br.com.william.parkinglot.exception.LotNotFoundException;
import br.com.william.parkinglot.fixture.LotFixture;
import br.com.william.parkinglot.model.dto.CarDTO;
import br.com.william.parkinglot.service.LotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"Não há mais vagas disponiveis!\",\n" +
                                          "    \"status\": 404,\n" +
                                          "    \"logref\": \"lot-not-found\"\n" +
                                          "}\n"));
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
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"teste\",\n" +
                                          "    \"status\": 409,\n" +
                                          "    \"logref\": \"conflict-lot\"\n" +
                                          "}\n"));
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
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"model:invalid-model,plate:invalid-plate\",\n" +
                                          "    \"status\": 400,\n" +
                                          "    \"logref\": \"bad-request\"\n" +
                                          "}\n"));
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
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"color:missing-color,model:missing-model,plate:missing-plate\",\n" +
                                          "    \"status\": 400,\n" +
                                          "    \"logref\": \"bad-request\"\n" +
                                          "}\n"));
    }

    // getOutOfParking - caminho feliz: valida chamada ao método da service
    @Test
    public void shouldGetOutCarOfParkingLot() throws Exception {
        // given
        var carPlate = "KGK1030";

        // when
        var actions = this.mockMvc.perform(delete(format("/lots/by-car-plate/%s", carPlate)));

        // then
        verify(this.lotService, times(1)).getOutOfParkingByCarPlate(carPlate);
        actions.andExpect(status().isAccepted());
    }

    // findByCarPlate - caminho feliz: encontra a vaga ocupada pelo carro
    @Test
    public void shouldFindOccupiedLotByCarPlate() throws Exception {
        // given
        var carPlate = "KGK1030";
        var lotNumber = 1;
        var lot = LotFixture.validOccupiedLot(lotNumber, carPlate);

        // when
        when(this.lotService.findByCarPlate(carPlate)).thenReturn(lot);
        var actions = this.mockMvc.perform(get(format("/lots/by-car-plate/%s", carPlate)));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().json(format("{\n" +
                                                 "    \"id\": \"%s\",\n" +
                                                 "    \"number\": 1,\n" +
                                                 "    \"car\": {\n" +
                                                 "        \"id\": \"%s\",\n" +
                                                 "        \"plate\": \"KGK1030\",\n" +
                                                 "        \"model\": \"Prisma\",\n" +
                                                 "        \"color\": \"Preto\"\n" +
                                                 "    }\n" +
                                                 "}\n", lot.getId(), lot.getCar().getId())));
    }

    /**
     * findByCarPlate - caminho infeliz: não encontra a vaga ocupada pelo carro e lança erro LotNotFoundException
     * Carro não está estacionado: KGK1030, lot-not-found
     */
    @Test
    public void shouldThrowErrorIfNotFindOccupiedLotByCarPlate() throws Exception {
        // given
        var carPlate = "KGK1030";

        // when
        when(this.lotService.findByCarPlate(carPlate)).thenThrow(new LotNotFoundException(carPlate));
        var actions = this.mockMvc.perform(get(format("/lots/by-car-plate/%s", carPlate)));

        // then
        actions
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"Carro não está estacionado: KGK1030\",\n" +
                                          "    \"status\": 404,\n" +
                                          "    \"logref\": \"lot-not-found\"\n" +
                                          "}\n"));
    }

    // findByNumber - caminho feliz: encontra a vaga pelo seu numero
    @Test
    public void shouldFindLotByNumber() throws Exception {
        // given
        var carPlate = "KGK1030";
        var lotNumber = 1;
        var lot = LotFixture.validOccupiedLot(lotNumber, carPlate);

        // when
        when(this.lotService.findByNumber(lotNumber)).thenReturn(lot);
        var actions = this.mockMvc.perform(get(format("/lots/%s", lotNumber)));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().json(format("{\n" +
                                                 "    \"id\": \"%s\",\n" +
                                                 "    \"number\": 1,\n" +
                                                 "    \"car\": {\n" +
                                                 "        \"id\": \"%s\",\n" +
                                                 "        \"plate\": \"KGK1030\",\n" +
                                                 "        \"model\": \"Prisma\",\n" +
                                                 "        \"color\": \"Preto\"\n" +
                                                 "    }\n" +
                                                 "}\n", lot.getId(), lot.getCar().getId())));
    }

    /**
     * findByNumber - caminho infeliz: não encontra a vaga pelo numero e lança erro LotNotFoundException
     * Vaga não existe: 1, lot-not-found
     */
    @Test
    public void shouldThrowErrorIfNotFindLotByNumber() throws Exception {
        // given
        var lotNumber = 1;

        // when
        when(this.lotService.findByNumber(lotNumber)).thenThrow(new LotNotFoundException(lotNumber));
        var actions = this.mockMvc.perform(get(format("/lots/%s", lotNumber)));

        // then
        actions
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\n" +
                                          "    \"message\": \"Vaga não existe: 1\",\n" +
                                          "    \"status\": 404,\n" +
                                          "    \"logref\": \"lot-not-found\"\n" +
                                          "}\n"));
    }

    // findAllLots - encontra todas, sem filtro
    @Test
    public void shouldFindAllLotsWithoutFilter() throws Exception {
        // given
        var lots = List.of(
                LotFixture.validOccupiedLot(1, "KGK1020")
        );

        // when
        when(this.lotService.findAll(null)).thenReturn(lots);
        var actions = this.mockMvc.perform(get("/lots"));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().json(format("[{\n" +
                                                 "    \"id\": \"%s\",\n" +
                                                 "    \"number\": 1,\n" +
                                                 "    \"car\": {\n" +
                                                 "        \"id\": \"%s\",\n" +
                                                 "        \"plate\": \"KGK1020\",\n" +
                                                 "        \"model\": \"Prisma\",\n" +
                                                 "        \"color\": \"Preto\"\n" +
                                                 "    }\n" +
                                                 "}]\n", lots.get(0).getId(), lots.get(0).getCar().getId())));
        verify(this.lotService, times(1)).findAll(null);
    }

    // findAllLots - encontra todas ocupadas, com filtro de ocupadas
    @Test
    public void shouldFindAllLotsFilteringOccupied() throws Exception {
        // given
        var lots = List.of(
                LotFixture.validOccupiedLot(1, "KGK1020")
        );

        // when
        when(this.lotService.findAll(false)).thenReturn(lots);
        var actions = this.mockMvc.perform(get("/lots?available=false"));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().json(format("[{\n" +
                                                 "    \"id\": \"%s\",\n" +
                                                 "    \"number\": 1,\n" +
                                                 "    \"car\": {\n" +
                                                 "        \"id\": \"%s\",\n" +
                                                 "        \"plate\": \"KGK1020\",\n" +
                                                 "        \"model\": \"Prisma\",\n" +
                                                 "        \"color\": \"Preto\"\n" +
                                                 "    }\n" +
                                                 "}]\n", lots.get(0).getId(), lots.get(0).getCar().getId())));
        verify(this.lotService, times(1)).findAll(false);
    }

    // findAllLots - encontra todas disponiveis, com filtro de disponiveis
    @Test
    public void shouldFindAllLotsFilteringAvailable() throws Exception {
        // given
        var lots = List.of(
                LotFixture.validAvailableLot(1)
        );

        // when
        when(this.lotService.findAll(true)).thenReturn(lots);
        var actions = this.mockMvc.perform(get("/lots?available=true"));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().json(format("[{\n" +
                                                 "    \"id\": \"%s\",\n" +
                                                 "    \"number\": 1\n" +
                                                 "}]\n", lots.get(0).getId())));
        verify(this.lotService, times(1)).findAll(true);
    }

    /**
     * findAll - caminho infeliz: valor de filtro invalido
     * lança erro MethodArgumentTypeMismatchException que é capturado na WebExceptionHandler
     */
    @Test
    public void shouldThrowErrorWhenFilteringByInvalidValue() throws Exception {
        // given
        var lots = List.of(
                LotFixture.validAvailableLot(1)
        );

        // when
        when(this.lotService.findAll(true)).thenReturn(lots);
        var actions = this.mockMvc.perform(get("/lots?available=invalid"));

        // then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                                          "    \"logref\": \"bad-request\",\n" +
                                          "    \"message\": \"Param invalid value: available:invalid\",\n" +
                                          "    \"status\": 400\n" +
                                          "}\n"));
    }
}