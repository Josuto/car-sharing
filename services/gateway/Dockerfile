FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY services/gateway/build/agents/opentelemetry-javaagent.jar /agents/opentelemetry-javaagent.jar
COPY services/gateway/build/libs/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/agents/opentelemetry-javaagent.jar"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
