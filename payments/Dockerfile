FROM alpine:3 AS agent
RUN wget -q -O /opentelemetry-javaagent.jar \
    https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/2.28.1/opentelemetry-javaagent-2.28.1.jar

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :payments:bootJar --no-daemon -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=agent /opentelemetry-javaagent.jar /agents/opentelemetry-javaagent.jar
COPY --from=builder /workspace/payments/build/libs/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/agents/opentelemetry-javaagent.jar"
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
