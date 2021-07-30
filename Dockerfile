FROM ghcr.io/graalvm/graalvm-ce:21.1.0
ARG JAR_FILE=target/external-dcsa-events-processor-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN curl -L -k 'https://github.com/DataDog/dd-trace-java/releases/download/v0.80.0/dd-java-agent.jar' --output dd-java-agent.jar
ENTRYPOINT ["java", "-javaagent:/dd-java-agent.jar", "-Xmx256m", "-jar","/app.jar"]
