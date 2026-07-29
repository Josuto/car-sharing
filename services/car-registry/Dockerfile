FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY services/car-registry/build/agents/opentelemetry-javaagent.jar /agents/opentelemetry-javaagent.jar
COPY services/car-registry/build/libs/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/agents/opentelemetry-javaagent.jar"
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
