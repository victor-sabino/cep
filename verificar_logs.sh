#!/bin/bash

# Configurações
CONTAINER_NAME=postgres_cep                # Nome do serviço ou container do PostgreSQL no Docker Compose
DB_NAME=cepdb                              # Nome do banco de dados
DB_USER=admin                              # Usuário do banco
TABLE_NAME=cep_log                         # Nome da tabela de logs

# Executa a consulta SQL no banco
echo "Consultando dados da tabela '$TABLE_NAME' no banco '$DB_NAME'..."
docker exec -it $CONTAINER_NAME \
  psql -U $DB_USER -d $DB_NAME \
  -c "SELECT * FROM $TABLE_NAME ORDER BY id DESC;"
