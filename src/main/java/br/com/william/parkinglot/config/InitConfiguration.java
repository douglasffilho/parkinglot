package br.com.william.parkinglot.config;

import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.repository.LotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@Configuration
@Profile("init")
public class InitConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(InitConfiguration.class);
    private static final int LOTS_AVAILABLE = 5;

    @Autowired
    LotRepository repository;

    @Override
    public void afterPropertiesSet() {
        log.info("INICIANDO VALIDAÇÃO DE VAGAS");
        final long contagemDeVagasNoBanco = this.repository.count();
        if (contagemDeVagasNoBanco != LOTS_AVAILABLE) {
            log.info("CRIANDO VAGAS NO BANCO DE DADOS");
            this.repository.deleteAll();
            IntStream
                    .rangeClosed(1, LOTS_AVAILABLE)
                    .mapToObj(Lot::new)
                    .forEach(this.repository::save);

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
}
