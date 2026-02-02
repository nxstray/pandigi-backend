FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 7860

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]