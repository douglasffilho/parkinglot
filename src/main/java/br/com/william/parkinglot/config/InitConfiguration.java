package br.com.william.parkinglot.config;

import br.com.william.parkinglot.entity.Car;
import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.repository.CarRepository;
import br.com.william.parkinglot.repository.LotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.IntStream;

@Configuration
@Profile("init")
public class InitConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(InitConfiguration.class);

    @Value("${LOTS_AVAILABLE:5}")
    Integer LOTS_AVAILABLE;

    @Autowired
    LotRepository lotsRepository;

    @Autowired
    CarRepository carRepository;

    @Override
    public void afterPropertiesSet() {
        log.info("INICIANDO VALIDAÇÃO DE VAGAS");
        final long contagemDeVagasNoBanco = this.lotsRepository.count();
        if (contagemDeVagasNoBanco != this.LOTS_AVAILABLE) {
            log.info("CRIANDO VAGAS NO BANCO DE DADOS");
            this.recreateCars();
            this.recreateLots();
        }
    }

    private void recreateCars() {
        this.carRepository.deleteAll();
        this.carRepository.saveAll(List.of(
                new Car("PDU1234", "Nissan Versa 2021", "Prata"),
                new Car("KGK1020", "Chevrolet Prisma 2015", "Preto"),
                new Car("PDU3213", "Nissan Versa 2022", "Prata"),
                new Car("KGK3020", "Chevrolet Cobalt 2015", "Preto"),
                new Car("KGI1010", "Jeep Renegade 2016", "Vermelho")
        ));
    }

    private void recreateLots() {
        this.lotsRepository.deleteAll();
        IntStream
                .rangeClosed(1, this.LOTS_AVAILABLE)
                .mapToObj(Lot::new)
                .forEach(this.lotsRepository::save);

//            IntStream
//                    .rangeClosed(1, LOTS_AVAILABLE)
//                    .mapToObj(number -> new Lot(number))
//                    .forEach(lot -> this.repository.save(lot));

//            for (int i = 1; i <= LOTS_AVAILABLE; i++) {
//                Lot newLot = new Lot(i);
//
//                this.repository.save(newLot);
//            }
    }
}
