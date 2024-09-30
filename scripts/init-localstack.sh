#!/bin/bash

until $(curl --output /dev/null --silent --head --fail http://localstack:4566/_localstack/health); do
    echo "Aguardando LocalStack iniciar..."
    sleep 5
done

echo "LocalStack está pronto. Inicializando secrets..."

aws --endpoint-url=http://localstack:4566 secretsmanager create-secret --name db-password --secret-string "MyP455" --region us-east-1

echo "Secret db-password criada com sucesso!"
