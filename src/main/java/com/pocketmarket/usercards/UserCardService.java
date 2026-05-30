package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardService;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCardService {

    private final UserCardRepository userCardRepository;
    private final UserRepository userRepository;
    private final CardService cardService;

    @Transactional
    public UserCardResponse createUserCard(User currentUser, @Valid UserCardRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Card card = cardService.findOrImportByExternalId(request.externalCardId());

        UserCard userCard = UserCardMapper.toEntity(request, user, card);
        userCard.setStatus(UserCardStatus.AVAILABLE);

        UserCard userCardSaved = userCardRepository.save(userCard);

        return UserCardMapper.toResponse(userCardSaved);
    }
}
