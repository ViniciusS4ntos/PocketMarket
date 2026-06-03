package com.pocketmarket.usercards;

import com.pocketmarket.user.User;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-cards")
@RequiredArgsConstructor
public class UserCardController {

    private final UserCardService userCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCardResponse createUserCard(@AuthenticationPrincipal User currentUser, @RequestBody @Valid UserCardRequest request) {
        return userCardService.createUserCard(currentUser, request);
    }

    @GetMapping("/me")
    public Page<UserCardResponse> getMyCards(@AuthenticationPrincipal User currentUser, @PageableDefault(size = 20) Pageable pageable) {
        return userCardService.getMyCards(currentUser, pageable);
    }

    @GetMapping("/{id}")
    public UserCardResponse getUserCard(@PathVariable UUID id) {
        return userCardService.getUserCard(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserCard(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        userCardService.deleteUserCard(id, currentUser);
    }
}
