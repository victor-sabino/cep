package com.desafio.cep.service;


import com.desafio.cep.exception.ExternalApiException;
import com.desafio.cep.model.CepLog;
import com.desafio.cep.model.CepResponse;
import com.desafio.cep.repository.CepLogRepository;
import com.desafio.cep.validator.CepValidator;
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
        CepValidator.validate(cep);
        return webClient.get()
                .uri("/ws/{cep}/json/", cep)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Erro desconhecido da API externa.")
                                .map(ExternalApiException::new)
                )
                .bodyToMono(CepResponse.class)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(response ->
                        repository.save(new CepLog(null, cep, response.getLogradouro(), LocalDateTime.now()))
                );
    }
}