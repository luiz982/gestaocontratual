package com.getinfo.gestaocontratual.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {

    @Value("${DATABASE_USERNAME}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD}")
    private String databasePassword;

    public void printDatabaseConfig() {
        System.out.println("Database Username: " + databaseUsername);
        System.out.println("Database Password: " + databasePassword);
    }
}