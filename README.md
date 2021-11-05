# external-dcsa-events-processor

### Mapstruct

For the mapstruct especially in this project, the lombok used was later than 1.18.16.

If you are using Lombok 1.18.16 or newer you also need to add lombok-mapstruct-binding in order to make Lombok and MapStruct work together.

```
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok-mapstruct-binding</artifactId>
            <version>0.2.0</version>
        </dependency>
```

#### Build application:
Build: `mvn clean install`
Build without running integration test: `mvn clean install -Dintegration-tests.skip`
Build without running unit test: `mvn clean install -Dunit-tests.skip `

### Running Integration Test

> mvn install failsafe:integration-test
