package com.pocketmarket;

import com.pocketmarket.cardcatalog.PokemonTcgProperties;
import com.pocketmarket.upload.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties({StorageProperties.class, PokemonTcgProperties.class})
@SpringBootApplication
@EnableScheduling
public class PocketmarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocketmarketApplication.class, args);
	}

}
