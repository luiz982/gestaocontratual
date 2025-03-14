# Etapa 1: Construção
FROM openjdk:17 AS build

# Instalar o Maven
RUN apt-get update && apt-get install -y maven

# Defina o diretório de trabalho
WORKDIR /app

# Copiar o código-fonte do projeto para o contêiner
COPY . .

# Rodar o Maven para construir o projeto
RUN mvn clean install

# Etapa 2: Execução
FROM openjdk:17-jdk-slim

# Expor a porta onde a aplicação vai rodar
EXPOSE 8080

# Copiar o JAR gerado na etapa de construção para o contêiner
COPY --from=build /app/target/gestaocontratual-0.0.1-SNAPSHOT.jar app.jar

# Definir o comando para rodar o aplicativo Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]