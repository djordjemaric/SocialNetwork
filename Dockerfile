FROM amazoncorretto:22
LABEL authors="levi-interns"

ARG JAR_FILE

COPY ${JAR_FILE} social-network.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "social-network.jar"]