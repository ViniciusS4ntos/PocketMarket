package com.pocketmarket.controllers;

import com.pocketmarket.auth.AuthController;
import com.pocketmarket.auth.AuthService;
import com.pocketmarket.auth.dto.LoginRequest;
import com.pocketmarket.auth.dto.LoginResponse;
import com.pocketmarket.auth.dto.RegisterRequest;
import com.pocketmarket.cardcatalog.CardCatalogController;
import com.pocketmarket.cardcatalog.CardCatalogService;
import com.pocketmarket.cardcatalog.dto.CardCatalogResponse;
import com.pocketmarket.cards.CardController;
import com.pocketmarket.cards.CardService;
import com.pocketmarket.cards.dto.CardResponseDTO;
import com.pocketmarket.collection.CollectionController;
import com.pocketmarket.collection.CollectionService;
import com.pocketmarket.collection.dto.CollectionRequest;
import com.pocketmarket.collection.dto.CollectionResponse;
import com.pocketmarket.favorite.FavoriteController;
import com.pocketmarket.favorite.FavoriteService;
import com.pocketmarket.favorite.dto.FavoriteResponse;
import com.pocketmarket.upload.controller.ImageUploadController;
import com.pocketmarket.upload.dto.ImageUploadResponse;
import com.pocketmarket.upload.service.ImageUploadService;
import com.pocketmarket.user.User;
import com.pocketmarket.usercards.UserCardController;
import com.pocketmarket.usercards.UserCardService;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import com.pocketmarket.enums.CardCondition;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerUnitTest {

    @Test
    void authControllerDelegatesToService() {
        AuthService service = mock(AuthService.class);
        AuthController controller = new AuthController(service);
        RegisterRequest register = new RegisterRequest("Ash", "ash@pm.com", "secret");
        LoginRequest login = new LoginRequest("ash@pm.com", "secret");
        when(service.register(register)).thenReturn(new LoginResponse("jwt", "ash@pm.com", "Ash"));
        when(service.login(login)).thenReturn(new LoginResponse("jwt", "ash@pm.com", "Ash"));

        assertThat(controller.register(register).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.login(login).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void cardCatalogControllerDelegatesToService() {
        CardCatalogService service = mock(CardCatalogService.class);
        CardCatalogController controller = new CardCatalogController(service);
        CardCatalogResponse response = catalogResponse();
        PageRequest pageable = PageRequest.of(0, 20);
        Page<CardCatalogResponse> page = new PageImpl<>(List.of(response), pageable, 1);
        when(service.findCards(pageable)).thenReturn(page);
        when(service.searchByName("charizard")).thenReturn(List.of(response));
        when(service.findByExternalId("base1-4")).thenReturn(response);

        assertThat(controller.findCards(pageable)).isSameAs(page);
        assertThat(controller.searchByName("charizard")).containsExactly(response);
        assertThat(controller.findByExternalId("base1-4")).isSameAs(response);
    }

    @Test
    void cardControllerDelegatesToService() {
        CardService service = mock(CardService.class);
        CardController controller = new CardController(service);
        UUID id = UUID.randomUUID();
        CardResponseDTO response = new CardResponseDTO(id, "ext", "Name", "set", "Set", "1", "Rare", null, null, null, "src", null, null);
        when(service.findAllCards()).thenReturn(List.of(response));
        when(service.findCard(id)).thenReturn(response);

        assertThat(controller.findAllCards()).containsExactly(response);
        assertThat(controller.findCard(id)).isSameAs(response);
        controller.delete(id);
        verify(service).delete(id);
    }

    @Test
    void collectionAndFavoriteControllersDelegateToServices() {
        User user = User.builder().id(UUID.randomUUID()).build();
        UUID cardId = UUID.randomUUID();
        CollectionService collectionService = mock(CollectionService.class);
        FavoriteService favoriteService = mock(FavoriteService.class);
        CollectionController collectionController = new CollectionController(collectionService);
        FavoriteController favoriteController = new FavoriteController(favoriteService);
        CollectionRequest collectionRequest = new CollectionRequest(cardId, 1);
        CollectionResponse collectionResponse = new CollectionResponse(UUID.randomUUID(), cardId, "Name", "Set", "Rare", null, null, null, 1, LocalDateTime.now());
        FavoriteResponse favoriteResponse = new FavoriteResponse(UUID.randomUUID(), cardId, "Name", "Set", "Rare", null, null, null);

        when(collectionService.addToCollection(collectionRequest, user)).thenReturn(collectionResponse);
        when(collectionService.listCollection(user)).thenReturn(List.of(collectionResponse));
        when(favoriteService.addFavorite(cardId, user)).thenReturn(favoriteResponse);
        when(favoriteService.listFavorites(user)).thenReturn(List.of(favoriteResponse));

        assertThat(collectionController.addToCollection(collectionRequest, user).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(collectionController.listCollection(user).getBody()).containsExactly(collectionResponse);
        assertThat(favoriteController.addFavorite(cardId, user).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(favoriteController.listFavorites(user).getBody()).containsExactly(favoriteResponse);

        collectionController.removeFromCollection(cardId, user);
        favoriteController.removeFavorite(cardId, user);
        verify(collectionService).removeFromCollection(cardId, user);
        verify(favoriteService).removeFavorite(cardId, user);
    }

    @Test
    void uploadAndUserCardControllersDelegateToServices() {
        ImageUploadService imageUploadService = mock(ImageUploadService.class);
        ImageUploadController imageUploadController = new ImageUploadController(imageUploadService);
        MockMultipartFile file = new MockMultipartFile("file", "card.png", "image/png", "abc".getBytes());
        ImageUploadResponse uploadResponse = new ImageUploadResponse("/url", "card.png", "image/png", 3);
        when(imageUploadService.uploadImage(file)).thenReturn(uploadResponse);

        assertThat(imageUploadController.uploadImage(file).getBody()).isEqualTo(uploadResponse);

        UserCardService userCardService = mock(UserCardService.class);
        UserCardController userCardController = new UserCardController(userCardService);
        User user = User.builder().id(UUID.randomUUID()).name("Ash").build();
        UserCardRequest request = new UserCardRequest("base1-4", CardCondition.NM, "proof.png");
        UserCardResponse response = new UserCardResponse(UUID.randomUUID(), null, user.getId(), user.getName(), CardCondition.NM, null, "proof.png", null, null, null);
        UUID userCardId = response.id();
        PageRequest pageable = PageRequest.of(0, 20);
        Page<UserCardResponse> page = new PageImpl<>(List.of(response), pageable, 1);
        when(userCardService.createUserCard(user, request)).thenReturn(response);
        when(userCardService.getMyCards(user, pageable)).thenReturn(page);
        when(userCardService.getUserCard(userCardId)).thenReturn(response);

        assertThat(userCardController.createUserCard(user, request)).isSameAs(response);
        assertThat(userCardController.getMyCards(user, pageable)).isSameAs(page);
        assertThat(userCardController.getUserCard(userCardId)).isSameAs(response);

        userCardController.deleteUserCard(userCardId, user);
        verify(userCardService).deleteUserCard(userCardId, user);
    }

    private CardCatalogResponse catalogResponse() {
        return new CardCatalogResponse("base1-4", "Charizard", "base1", "Base", "4", "Rare Holo", "small", "large");
    }
}
