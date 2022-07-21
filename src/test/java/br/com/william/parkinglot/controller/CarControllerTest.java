package br.com.william.parkinglot.controller;

import br.com.william.parkinglot.SpringBootApplicationTest;
import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.exception.CarNotFoundException;
import br.com.william.parkinglot.fixture.CarFixture;
import br.com.william.parkinglot.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        Car car = CarFixture.validCar(carPlate);

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

    // findAll - caminho feliz: pagina, tamanho da pagina e filtro
    // findAll - caminho feliz: pagina, tamanho da pagina, sem filtro
    // findAll - caminho feliz: pagina, sem tamanho da pagina, sem filtro
    // findAll - caminho feliz: sem pagina, sem tamanho da pagina e sem filtro
    // findAll - caminho infeliz: pagina com tipo de valor incorreto
    // findAll - caminho infeliz: tamanho da pagina com tipo de valor incorreto
    // findAll - caminho infeliz: passando um array no lugar da pagina (GlobalErrorHandler para o erro IllegalArgumentException)
    // findAll - caminho infeliz: passando um objeto no lugar da pagina (GlobalErrorHandler para o erro IllegalArgumentException)
}