package com.desafio.cep.model;

import lombok.Data;

@Data
public class CepResponse {
    private String cep;
    private String logradouro;
    private boolean erro;

}