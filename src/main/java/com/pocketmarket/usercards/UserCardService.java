package com.pocketmarket.usercards;

import com.pocketmarket.cards.Card;
import com.pocketmarket.cards.CardService;
import com.pocketmarket.enums.UserCardStatus;
import com.pocketmarket.exceptions.ForbiddenException;
import com.pocketmarket.exceptions.NotFoundException;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import com.pocketmarket.usercards.dto.request.UserCardRequest;
import com.pocketmarket.usercards.dto.response.UserCardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCardService {

    private final UserCardRepository userCardRepository;
    private final UserRepository userRepository;
    private final CardService cardService;

    @Transactional
    public UserCardResponse createUserCard(User currentUser, @Valid UserCardRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Card card = cardService.findOrImportByExternalId(request.externalCardId());

        UserCard userCard = UserCardMapper.toEntity(request, user, card);
        userCard.setStatus(UserCardStatus.AVAILABLE);

        UserCard userCardSaved = userCardRepository.save(userCard);
        return UserCardMapper.toResponse(userCardSaved);
    }

    public Page<UserCardResponse> getMyCards(User currentUser, Pageable pageable) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Page<UserCard> userCardsPage = userCardRepository.findByOwner(user, pageable);
        return userCardsPage.map(UserCardMapper::toResponse);
    }

    public UserCardResponse getUserCard(UUID id) {
        UserCard userCard = userCardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserCard não encontrado"));
        return UserCardMapper.toResponse(userCard);
    }

    @Transactional
    public void deleteUserCard(UUID id, User currentUser) {
        UserCard userCard = userCardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserCard não encontrado"));

        if (!userCard.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar este UserCard");
        }

        userCardRepository.delete(userCard);
    }
}