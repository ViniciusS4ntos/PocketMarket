package com.pocketmarket.usercards;

import com.pocketmarket.user.User;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
