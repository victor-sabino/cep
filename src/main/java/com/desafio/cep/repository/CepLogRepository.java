package com.desafio.cep.repository;


import com.desafio.cep.model.CepLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepLogRepository extends JpaRepository<CepLog, Long> {
}