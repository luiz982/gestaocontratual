package com.getinfo.gestaocontratual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class GestaocontratualApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(GestaocontratualApplication.class, args);
	}

	private static void loadEnvVariables() {

		try {
			String envFilePath = ".env";
			System.out.println("📂 Tentando ler o arquivo: " + Paths.get(envFilePath).toAbsolutePath());

			if (!Files.exists(Paths.get(envFilePath))) {
				return;
			}

			List<String> lines = Files.readAllLines(Paths.get(envFilePath));
			Map<String, String> envVariables = lines.stream()
					.filter(line -> !line.startsWith("#") && line.contains("="))
					.map(line -> line.split("=", 2))
					.collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));

			envVariables.forEach((key, value) -> {
				System.setProperty(key, value);
				System.out.println("✅ Setando variável: " + key + " = " + value);
			});


		} catch (IOException e) {
			System.out.println("❌ Erro ao ler o .env: " + e.getMessage());
		}
	}

}
