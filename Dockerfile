# Usando uma imagem base do JDK 17
FROM openjdk:17-jdk-slim

# Definindo o diretório de trabalho no container
WORKDIR /app

# Instalando o PostgreSQL para ter acesso ao pg_isready
RUN apt-get update && apt-get install -y postgresql-client && apt-get clean

# Copiando o wrapper e arquivos de configuração do Gradle
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copie o script de espera para o contêiner
COPY ./scripts/wait-for-postgres.sh /usr/local/bin/wait-for-postgres

# Dando permissão de execução ao script
RUN chmod +x /usr/local/bin/wait-for-postgres

# Baixando as dependências para otimizar o cache
RUN ./gradlew build -x test --no-daemon || true

# Copiando o código da aplicação para o container
COPY . .

# Buildando a aplicação (ignora os testes)
RUN ./gradlew build -x test --no-daemon

# Expondo a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["wait-for-postgres", "java", "-jar", "build/libs/spring-security-key-vault-example-0.0.1-SNAPSHOT.jar"]
