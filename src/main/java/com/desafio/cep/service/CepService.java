package com.desafio.cep.service;


import com.desafio.cep.model.CepLog;
import com.desafio.cep.model.CepResponse;
import com.desafio.cep.repository.CepLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
public class CepService {
    private final WebClient webClient;
    private final CepLogRepository repository;

    @Autowired
    public CepService(WebClient webClient, CepLogRepository repository) {
        this.webClient = webClient;
        this.repository = repository;
    }

    public Mono<CepResponse> getCep(String cep) {
        return webClient.get()
                .uri("/ws/{cep}/json/", cep)
                .retrieve()
                .bodyToMono(CepResponse.class)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(response ->
                        repository.save(new CepLog(null, cep, response.getLogradouro(), LocalDateTime.now()))
                );
    }
}