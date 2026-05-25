package com.pocketmarket;

import com.pocketmarket.upload.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(StorageProperties.class)
@SpringBootApplication
public class PocketmarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocketmarketApplication.class, args);
	}

}
