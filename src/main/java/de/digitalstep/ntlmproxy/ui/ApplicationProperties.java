package de.digitalstep.ntlmproxy.ui;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private static final ApplicationProperties INSTANCE = new ApplicationProperties();

    public static ApplicationProperties applicationProperties() {
        return INSTANCE;
    }

    private final Properties properties = new Properties();

    private ApplicationProperties() {
        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getApplicationName() {
        return get("application.name");
    }

    public String getApplicationVersion() {
        return get("application.version");
    }

    private String get(String property) {
        return properties.getProperty(property);
    }

}
