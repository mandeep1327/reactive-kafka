package util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Data
public class EnvironmentReader {
    private String apiRoot;
    private String apiBasePath;
    private boolean wireMockEnabled;
    private boolean useLocalWireMocks;
    private int wireMockVgmGCSSServicesPort;
    private int wireMockPartiesServicesPort;
    private int wireMockForgeRockServicesPort;
    private int wireMockEquipmentServicesPort;
    private int wireMockIamAuthenticationServicesPort;
    private InputStream inputStream;

    public EnvironmentReader() {
        try (InputStream input = new FileInputStream("src/integrationtest/resources/testConfiguration.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.apiRoot = prop.getProperty("api.root");
        } catch (IOException e) {
            log.error("Exception: " + e);
        }
    }
}