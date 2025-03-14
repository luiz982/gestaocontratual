package com.getinfo.gestaocontratual.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class EnvConfig {

    public EnvConfig() {
        System.setProperty("DATABASE_USERNAME", "avnadmin");
        System.setProperty("DATABASE_PASSWORD", "AVNS_D88x-Hhe-fe59uzdu5n");
    }
}