package util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class TestDataUtils {

    public static String loadDataJson(String dataFilePath) {
        try {
            String pathToPreconditionFiles = "src/integrationtest/resources/__files/";
            var inputStream = new FileSystemResource(pathToPreconditionFiles + dataFilePath).getInputStream();
            return new String(inputStream.readAllBytes());
        } catch (FileNotFoundException e) {
            log.error("Could not finddata {}", e.getMessage());
        } catch (IOException e) {
            log.error("Could not load file with data {}", e.getMessage());
        }
        return null;
    }
}
