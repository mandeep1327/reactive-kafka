# external-dcsa-events-processor



## Running Integration Test

# Start docker locally 
> mvn install failsafe:integration-test


For the mapstruct especially in this project, the lombok used was later than 1.18.16.

If you are using Lombok 1.18.16 or newer you also need to add lombok-mapstruct-binding in order to make Lombok and MapStruct work together.

```
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok-mapstruct-binding</artifactId>
            <version>0.2.0</version>
        </dependency>
```

## Running Integration Test

> mvn install failsafe:integration-test
