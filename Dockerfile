#Etapa 1) Instalar o Java JDK (Compilar o projeto) dentro do Container do Docker
FROM eclipse-temurin:21-jdk as build

#Etapa 2) Criar uma pasta dentro do container para guardar os arquivos do projeto
WORKDIR /app

#Etapa 3) Copiar todos os arquivos do projeto para dentro da pasta
COPY . .

#Etapa 4) Compilar o código do projeto (BUILD)
RUN ./mvnw clean package -DskipTests

#Etapa 5) Instalar o Java JRE (Executar o projeto) dentro do Container do Docker
FROM eclipse-temurin:21-jre as deploy

#Etapa 6) Criar uma pasta para guardar os arquivos
WORKDIR /app

#Etapa 7) Copiar os arquivos do BUILD para dentro da pasta
COPY --from=build /app/target/api-usuarios-*.jar app.jar

#Etapa 8) Configurar a porta em que o projeto irá rodar
EXPOSE 8082

#Etapa 9) Executar o projeto
ENTRYPOINT ["java", "-jar", "app.jar"]
