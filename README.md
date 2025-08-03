# 🧭 Projeto Spring Boot - Consulta de CEP com Log em Banco

Este projeto demonstra uma aplicação simples usando **Spring Boot + Java 21** que realiza a **consulta de um CEP** via
API externa (ViaCEP) e **registra os logs de busca em um banco PostgreSQL** usando **JPA**. A infraestrutura do banco é
gerenciada via **Docker**.

---

## 📌 Visão Geral da Solução

```
┌────────────┐         ┌────────────────────────────┐
│ Usuário    │ ─────▶ │ Endpoint REST /cep/{cep}   │
└────────────┘         └────────────────────────────┘
                             │
                             ▼
                   ┌────────────────────┐
                   │ Service CepService │
                   └────────────────────┘
                             │
                             ▼
              ┌────────────────────────────┐
              │ API ViaCEP (https externa) │
              └────────────────────────────┘
                             │
                             ▼
              ┌────────────────────────────┐
              │ Persistência Log (JPA)     │
              │ Tabela cep_log no banco    │
              └────────────────────────────┘
```

---

## ⚙️ Tecnologias Utilizadas

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Docker + Docker Compose
- API pública [ViaCEP](https://viacep.com.br)

---

## 🚀 Como Executar

1. **Subir o banco de dados com Docker:**

```bash  

docker-compose up -d
```

2. **Executar o projeto:**

```bash

./mvnw spring-boot:run
```

3. **Testar a API:**

```bash

curl http://localhost:8080/cep/01001000
```

---

## 🧪 Exemplo de Resposta

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé"
}
```

Além disso, um registro é salvo na tabela `cep_log` contendo o CEP e logradouro consultado.

---

## 🔍 Verificar os dados salvos no banco

Este projeto inclui um **script automatizado** para consultar a tabela `cep_log` no banco de dados PostgreSQL via
Docker.

### 📄 Arquivo: `verificar_logs.sh`

#### ✅ Como usar:

1. **Tornar o script executável:**

```bash

chmod +x verificar_logs.sh
```

2. **Executar via terminal do IntelliJ ou linha de comando:**

```bash

./verificar_logs.sh
```

Isso executará a seguinte consulta no banco de dados `cepdb`:

```sql
SELECT *
FROM cep_log
ORDER BY id DESC;
```

---

### ▶️ Executar todos os testes:

```bash

./mvnw clean test
```

> O Maven irá baixar as dependências e executar os testes automaticamente.
---

## 📂 Estrutura de Pastas

```
src
├── main
│   ├── java/com/example/cep
│   │   ├── CepApplication.java
│   │   ├── controller/CepController.java
│   │   ├── service/CepService.java
│   │   ├── model/CepResponse.java, CepLog.java
│   │   └── repository/CepLogRepository.java
│   └── resources/application.yml
└── docker-compose.yml
```

---

## 📦 Possíveis Melhorias

- Integração com AWS SQS
- Monitoramento (Actuator, Prometheus)
- Interface Web para visualização dos logs

---