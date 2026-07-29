FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY services/car-booking/build/agents/opentelemetry-javaagent.jar /agents/opentelemetry-javaagent.jar
COPY services/car-booking/build/libs/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/agents/opentelemetry-javaagent.jar"
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
