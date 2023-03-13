package ru.kazov.collectivepurchases.worker.common;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
public class AppProperties {

    private Properties properties;

    @Setter
    @Getter
    private boolean running = true;
    private final String filename = "conf.properties";

    @PostConstruct
    private void onInit() {
        loadProperties();
    }

    public String getServer() {
        return properties.getProperty("server");
    }

    public void setServer(String server) {
        properties.setProperty("server", server);
        saveProperties();
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless"));
    }

    public void setHeadless(boolean headless) {
        properties.setProperty("headless", String.valueOf(headless));
        saveProperties();
    }

    public String getToken() {
        return properties.getProperty("token");
    }

    public void setToken(String token) {
        properties.setProperty("token", token);
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(filename)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveProperties() {
        try (OutputStream output = new FileOutputStream(filename)) {
            properties.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
