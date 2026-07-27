FROM gradle:9.4.1-jdk21-alpine AS builder
WORKDIR /workspace
COPY . .
RUN gradle :psp-stub:bootJar --no-daemon -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/services/psp-stub/build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
