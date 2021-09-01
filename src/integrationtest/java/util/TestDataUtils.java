package util;

import MSK.com.gems.GEMSPubType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static GEMSPubType getGemsData(String jsonPayload){
        ObjectMapper mapper = new ObjectMapper();
        GEMSPubType gemsPayload = null;
        try {
            gemsPayload = mapper.readValue(jsonPayload, GEMSPubType.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return gemsPayload;
    }
}
