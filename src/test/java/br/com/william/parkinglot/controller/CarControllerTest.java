package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.exception.CarNotFoundException;
import br.com.william.parkinglot.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static br.com.william.parkinglot.fixture.CarFixture.validCar;
import static java.lang.String.format;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CarControllerTest extends SpringBootApplicationTest {

    @MockBean
    private CarService carServiceMock;

    @Test
    public void shouldFindCarByPlateWithStatusOK() throws Exception {
        // given
        var carPlate = "KGK1020";
        Car car = validCar(carPlate);

        // when
        when(this.carServiceMock.findByPlate(carPlate)).thenReturn(car);

        // then
        this.mockMvc
                .perform(get(format("/cars/%s", carPlate)))
                .andExpect(status().isOk())
                .andExpect(content().json(format("    {\n" +
                                                 "        \"id\":\"%s\",\n" +
                                                 "        \"plate\":\"KGK1020\",\n" +
                                                 "        \"model\":\"Prisma\",\n" +
                                                 "        \"color\":\"Preto\"\n" +
                                                 "    }\n", car.getId()), true));
//        var jsonResponse = this.mockMvc
//                .perform(get(format("/cars/%s", carPlate)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Car response = new ObjectMapper().readValue(jsonResponse, Car.class);
//
//        Assertions.assertEquals(car, response);
    }

    @Test
    public void shouldNotFindCarByPlateWithStatusNotFound() throws Exception {
        // given
        var carPlate = "KGK3020";

        // when
        when(this.carServiceMock.findByPlate(carPlate))
                .thenThrow(new CarNotFoundException(format("car not found by plate: %s", carPlate)));

        // then
        this.mockMvc
                .perform(get(format("/cars/%s", carPlate)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(format("    {\n" +
                                                 "        \"status\":404,\n" +
                                                 "        \"message\":\"car not found by plate: %s\",\n" +
                                                 "        \"logref\":\"car-not-found\"\n" +
                                                 "    }\n", carPlate), true));
//        var jsonResponse = this.mockMvc
//                .perform(get(format("/cars/%s", carPlate)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Car response = new ObjectMapper().readValue(jsonResponse, Car.class);
//
//        Assertions.assertEquals(car, response);
    }

    // findAll - caminho feliz: pagina, tamanho da pagina e sem filtro
    @Test
    public void shouldFindCarsWithPageAndSizeAndWithoutFilter() throws Exception {
        // given
        var cars = List.of(
                validCar("KGK1020"),
                validCar("KGK2030")
        );
        var pageRequest = PageRequest.of(0, 10);
        var page = new PageImpl<>(cars, pageRequest, cars.size());
        var filter = "";

        // when
        when(this.carServiceMock.paginateCars(pageRequest.getPageNumber() + 1, pageRequest.getPageSize(), filter))
                .thenReturn(page);

        // then
        var url = format("/cars?page=%s&size=%s",
                pageRequest.getPageNumber() + 1,
                pageRequest.getPageSize()
        );
        validateRequest(url, cars);
    }

    // findAll - caminho feliz: pagina, tamanho da pagina e filtro
    @Test
    public void shouldFindCarsWithPageGreaterThenOneAndSizeGreaterThenOneAndFilter() throws Exception {
        // given
        var cars = List.of(
                validCar("KGK1020"),
                validCar("KGK2030")
        );
        var pageRequest = PageRequest.of(0, 10);
        var page = new PageImpl<>(cars, pageRequest, cars.size());
        var filter = "pret";

        // when
        when(this.carServiceMock.paginateCars(pageRequest.getPageNumber() + 1, pageRequest.getPageSize(), filter))
                .thenReturn(page);

        // then
        var url = format("/cars?page=%s&size=%s&filter=%s",
                pageRequest.getPageNumber(),
                0,
                filter
        );
        validateRequest(url, cars);
    }

    // findAll - caminho feliz: pagina, sem tamanho da pagina, sem filtro
    @Test
    public void shouldFindCarsWithPageAndWithoutSizeAndWithoutFilter() throws Exception {
        // given
        var cars = List.of(
                validCar("KGK1020"),
                validCar("KGK2030")
        );
        var pageRequest = PageRequest.of(0, 10);
        var page = new PageImpl<>(cars, pageRequest, cars.size());
        var filter = "";

        // when
        when(this.carServiceMock.paginateCars(pageRequest.getPageNumber() + 1, pageRequest.getPageSize(), filter))
                .thenReturn(page);

        // then
        var url = format("/cars?page=%s", pageRequest.getPageNumber());
        validateRequest(url, cars);
    }

    // findAll - caminho feliz: sem pagina, sem tamanho da pagina e sem filtro
    @Test
    public void shouldFindCarsWithoutPageAndWithoutSizeAndWithoutFilter() throws Exception {
        // given
        var cars = List.of(
                validCar("KGK1020"),
                validCar("KGK2030")
        );
        var pageRequest = PageRequest.of(0, 10);
        var page = new PageImpl<>(cars, pageRequest, cars.size());
        var filter = "";

        // when
        when(this.carServiceMock.paginateCars(pageRequest.getPageNumber() + 1, pageRequest.getPageSize(), filter))
                .thenReturn(page);

        // then
        var url = "/cars";
        validateRequest(url, cars);
    }

    // findAll - caminho infeliz: pagina com tipo de valor incorreto
    @Test
    public void shouldThrowErrorWhenTryingToMakeInvalidPageNumberRequest() throws Exception {
        // given
        var url = "/cars?page=a";

        // then
        validateBadRequest(url, "Param invalid value: page:a");
    }

    // findAll - caminho infeliz: tamanho da pagina com tipo de valor incorreto
    @Test
    public void shouldThrowErrorWhenTryingToMakeInvalidPageSizeRequest() throws Exception {
        // given
        var url = "/cars?size=a";

        // then
        validateBadRequest(url, "Param invalid value: size:a");
    }

    private void validateRequest(String url, List<Car> cars) throws Exception {
        this.mockMvc
                .perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(format("    {\n" +
                                                 "      \"content\": [\n" +
                                                 "        {\n" +
                                                 "          \"id\": \"%s\",\n" +
                                                 "          \"plate\": \"KGK1020\",\n" +
                                                 "          \"model\": \"Prisma\",\n" +
                                                 "          \"color\": \"Preto\"\n" +
                                                 "        },\n" +
                                                 "        {\n" +
                                                 "          \"id\": \"%s\",\n" +
                                                 "          \"plate\": \"KGK2030\",\n" +
                                                 "          \"model\": \"Prisma\",\n" +
                                                 "          \"color\": \"Preto\"\n" +
                                                 "        }\n" +
                                                 "      ],\n" +
                                                 "      \"pageable\": {\n" +
                                                 "        \"sort\": {\n" +
                                                 "          \"sorted\": false,\n" +
                                                 "          \"unsorted\": true,\n" +
                                                 "          \"empty\": true\n" +
                                                 "        },\n" +
                                                 "        \"pageNumber\": 0,\n" +
                                                 "        \"pageSize\": 10,\n" +
                                                 "        \"offset\": 0,\n" +
                                                 "        \"paged\": true,\n" +
                                                 "        \"unpaged\": false\n" +
                                                 "      },\n" +
                                                 "      \"totalPages\": 1,\n" +
                                                 "      \"totalElements\": 2,\n" +
                                                 "      \"last\": true,\n" +
                                                 "      \"numberOfElements\": 2,\n" +
                                                 "      \"sort\": {\n" +
                                                 "        \"sorted\": false,\n" +
                                                 "        \"unsorted\": true,\n" +
                                                 "        \"empty\": true\n" +
                                                 "      },\n" +
                                                 "      \"first\": true,\n" +
                                                 "      \"number\": 0,\n" +
                                                 "      \"size\": 10,\n" +
                                                 "      \"empty\": false\n" +
                                                 "    }\n", cars.get(0).getId(), cars.get(1).getId()), true));
    }

    private void validateBadRequest(String url, String errorMessage) throws Exception {
        int statusCode = HttpStatus.BAD_REQUEST.value();
        this.mockMvc
                .perform(get(url))
                .andExpect(status().is(statusCode))
                .andExpect(content().json(format("    {\n" +
                                                 "      \"logref\":\"bad-request\",\n" +
                                                 "      \"message\":\"%s\",\n" +
                                                 "      \"status\":%s\n" +
                                                 "    }\n", errorMessage, statusCode), true));
    }
}