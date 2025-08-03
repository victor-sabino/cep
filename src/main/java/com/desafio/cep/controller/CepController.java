package com.desafio.cep.controller;


import com.desafio.cep.model.CepResponse;
import com.desafio.cep.service.CepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
public class CepController {
    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public CepResponse buscarCep(@PathVariable String cep) {
        return cepService.getCep(cep).block();
    }
}