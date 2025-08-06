package com.desafio.cep.validator;

import com.desafio.cep.exception.InvalidCepException;

public class CepValidator {

    private static final String ONLY_NUMBERS_CEP_REGEX = "^\\d{8}$";

    public static void validate(String cep) {
        if (cep == null || !cep.matches(ONLY_NUMBERS_CEP_REGEX)) {
            throw new InvalidCepException("""
                        {
                            "CEP inválido. Deve conter exatamente 8 dígitos numéricos."
                        }
                    """);
        }
    }
}
