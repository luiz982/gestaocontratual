package com.getinfo.gestaocontratual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class GestaocontratualApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaocontratualApplication.class, args);
	}


}
