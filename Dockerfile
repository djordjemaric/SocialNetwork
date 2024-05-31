FROM amazoncorretto:17
LABEL authors="levi-interns"
ARG JAR_FILE

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.4.0/opentelemetry-javaagent.jar /otel/otel-javaagent.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/otel/otel-javaagent.jar"

COPY ${JAR_FILE} social-network.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar", "social-network.jar"]