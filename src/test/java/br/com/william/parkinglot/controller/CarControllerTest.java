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
                .perform(get("/cars/%s".formatted(carPlate)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "id":"%s",
                                "plate":"KGK1020",
                                "model":"Prisma",
                                "color":"Preto"
                            }
                        """.formatted(car.getId()), true));
//        var jsonResponse = this.mockMvc
//                .perform(get("/cars/%s".formatted(carPlate)))
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
                .thenThrow(new CarNotFoundException("car not found by plate: %s".formatted(carPlate)));

        // then
        this.mockMvc
                .perform(get("/cars/%s".formatted(carPlate)))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                            {
                                "status":404,
                                "message":"car not found by plate: %s",
                                "logref":"car-not-found"
                            }
                        """.formatted(carPlate), true));
//        var jsonResponse = this.mockMvc
//                .perform(get("/cars/%s".formatted(carPlate)))
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
        var url = "/cars?page=%s&size=%s".formatted(
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
        var url = "/cars?page=%s&size=%s&filter=%s".formatted(
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
        var url = "/cars?page=%s".formatted(pageRequest.getPageNumber());
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
                .andExpect(content().json("""
                            {
                              "content": [
                                {
                                  "id": "%s",
                                  "plate": "KGK1020",
                                  "model": "Prisma",
                                  "color": "Preto"
                                },
                                {
                                  "id": "%s",
                                  "plate": "KGK2030",
                                  "model": "Prisma",
                                  "color": "Preto"
                                }
                              ],
                              "pageable": {
                                "sort": {
                                  "sorted": false,
                                  "unsorted": true,
                                  "empty": true
                                },
                                "pageNumber": 0,
                                "pageSize": 10,
                                "offset": 0,
                                "paged": true,
                                "unpaged": false
                              },
                              "totalPages": 1,
                              "totalElements": 2,
                              "last": true,
                              "numberOfElements": 2,
                              "sort": {
                                "sorted": false,
                                "unsorted": true,
                                "empty": true
                              },
                              "first": true,
                              "number": 0,
                              "size": 10,
                              "empty": false
                            }
                        """.formatted(cars.get(0).getId(), cars.get(1).getId()), true));
    }

    private void validateBadRequest(String url, String errorMessage) throws Exception {
        int statusCode = HttpStatus.BAD_REQUEST.value();
        this.mockMvc
                .perform(get(url))
                .andExpect(status().is(statusCode))
                .andExpect(content().json("""
                            {
                              "logref":"bad-request",
                              "message":"%s",
                              "status":%s
                            }
                        """.formatted(errorMessage, statusCode), true));
    }
}