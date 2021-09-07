FROM ghcr.io/graalvm/graalvm-ce:21.1.0
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar","/app.jar"]
