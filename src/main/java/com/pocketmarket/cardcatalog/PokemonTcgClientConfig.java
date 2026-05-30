package com.pocketmarket.cardcatalog;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PokemonTcgClientConfig {

    @Bean
    public RestClient pokemonTcgRestClient(PokemonTcgProperties properties) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.baseUrl());

        if (properties.apiKey() != null && !properties.apiKey().isBlank()) {
            builder.defaultHeader("X-Api-Key", properties.apiKey());
        }

        return builder.build();
    }
}
