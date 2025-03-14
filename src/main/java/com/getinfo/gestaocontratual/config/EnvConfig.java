package com.getinfo.gestaocontratual.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void init() {
        try {
            String envFilePath = ".env";
            System.out.println("📂 Tentando ler o arquivo: " + Paths.get(envFilePath).toAbsolutePath());

            if (!Files.exists(Paths.get(envFilePath))) {
                System.out.println("❌ ERRO: O arquivo .env não foi encontrado!");
                return;
            }

            List<String> lines = Files.readAllLines(Paths.get(envFilePath));
            if (lines.isEmpty()) {
                System.out.println("⚠️ Atenção: O arquivo .env está vazio!");
            }

            System.out.println("✅ Arquivo .env encontrado e lido com sucesso!");
            lines.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("❌ Erro ao ler o .env: " + e.getMessage());
        }
    }
}
