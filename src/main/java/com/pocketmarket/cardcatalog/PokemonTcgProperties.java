package com.pocketmarket.cardcatalog;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pokemon-tcg")
public record PokemonTcgProperties(
        String baseUrl,
        String apiKey
) {
}
