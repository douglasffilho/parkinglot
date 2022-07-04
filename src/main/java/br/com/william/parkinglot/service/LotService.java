package br.com.william.parkinglot.service;

import br.com.william.parkinglot.entity.Lot;
import br.com.william.parkinglot.repository.LotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@Service
public class LotService {
    private static final Logger log = LoggerFactory.getLogger(LotService.class);

    private static final int LOTS_AVAILABLE = 10;

    private final LotRepository repository;

    public LotService(final LotRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        log.info("INICIANDO VALIDAÇÃO DE VAGAS");
        final long contagemDeVagasNoBanco = this.repository.count();
        if (contagemDeVagasNoBanco != LOTS_AVAILABLE) {
            log.info("CRIANDO VAGAS NO BANCO DE DADOS");
            this.repository.deleteAll();
            IntStream
                    .rangeClosed(1, LOTS_AVAILABLE)
                    .mapToObj(Lot::new)
                    .forEach(this.repository::save);

//            for (int i = 1; i <= LOTS_AVAILABLE; i++) {
//                Lot newLot = new Lot(i);
//
//                this.repository.save(newLot);
//            }
        }
    }
}
