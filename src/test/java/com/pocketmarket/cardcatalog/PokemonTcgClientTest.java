package com.pocketmarket.cardcatalog;

import com.pocketmarket.cardcatalog.dto.PokemonTcgCardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PokemonTcgClientTest {

    private MockRestServiceServer server;
    private PokemonTcgClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.pokemontcg.io/v2");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new PokemonTcgClient(builder.build());
    }

    @Test
    void searchByNameReturnsEmptyListWhenNameIsBlank() {
        assertThat(client.searchByName(null)).isEmpty();
        assertThat(client.searchByName(" ")).isEmpty();
    }

    @Test
    void searchByNameReturnsDataOrEmptyList() {
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards?q=name:charizard&pageSize=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"data":[{"id":"base1-4","name":"Charizard","number":"4","rarity":"Rare Holo","set":{"id":"base1","name":"Base"},"images":{"small":"small","large":"large"}}],"page":1,"pageSize":10,"count":1,"totalCount":1}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        List<PokemonTcgCardResponse> response = client.searchByName("charizard");

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().id()).isEqualTo("base1-4");
        server.verify();
    }

    @Test
    void searchByNameReturnsEmptyListWhenResponseDataIsNull() {
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards?q=name:missing&pageSize=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"data":null,"page":1,"pageSize":10,"count":0,"totalCount":0}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        assertThat(client.searchByName("missing")).isEmpty();
        server.verify();
    }

    @Test
    void findByExternalIdReturnsNullWhenBlankOrNotFound() {
        assertThat(client.findByExternalId(null)).isNull();
        assertThat(client.findByExternalId(" ")).isNull();

        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards/missing"))
                .andRespond(withResourceNotFound());

        assertThat(client.findByExternalId("missing")).isNull();
        server.verify();
    }

    @Test
    void findByExternalIdReturnsData() {
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards/base1-4"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"data":{"id":"base1-4","name":"Charizard","number":"4","rarity":"Rare Holo"}}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        PokemonTcgCardResponse response = client.findByExternalId("base1-4");

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Charizard");
        server.verify();
    }

    @Test
    void findCardsSendsPaginationAndSort() {
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards?page=1&pageSize=10&orderBy=-name"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body("""
                                {"data":[],"page":1,"pageSize":10,"count":0,"totalCount":0}
                                """));

        var response = client.findCards(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name")));

        assertThat(response.data()).isEmpty();
        server.verify();
    }

    @Test
    void findCardsHandlesUnsortedAndMultipleSorts() {
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards?page=1&pageSize=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"data":[],"page":1,"pageSize":10,"count":0,"totalCount":0}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        client.findCards(PageRequest.of(0, 10));
        server.verify();

        server.reset();
        server.expect(once(), requestTo("https://api.pokemontcg.io/v2/cards?page=1&pageSize=10&orderBy=name,-number"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"data":[],"page":1,"pageSize":10,"count":0,"totalCount":0}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        client.findCards(PageRequest.of(0, 10, Sort.by("name").and(Sort.by(Sort.Direction.DESC, "number"))));
        server.verify();
    }
}
