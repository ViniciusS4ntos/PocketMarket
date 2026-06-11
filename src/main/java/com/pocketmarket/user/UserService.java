package com.pocketmarket.user;

import com.pocketmarket.exceptions.NotFoundException;
import com.pocketmarket.user.dtos.in.UserCreditsRequest;
import com.pocketmarket.user.dtos.out.UserCreditsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // visualizar perfil
    public User buscarPerfilPorId(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil nao encontrado!"));
    }

    // atualizar Perfil
    public User atualizarPerfil(UUID id, User user){

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        return userRepository.save(atualizarUsuario(entity,user));

    }

    public User atualizarUsuario(User entity, User dto){
        return User.builder()
                .id(entity.getId())
                .name(dto.getName() != null ? dto.getName() : entity.getName())
                .email(dto.getEmail() != null  ? dto.getUsername() : entity.getEmail())
                .password(dto.getPassword() !=  null ? passwordEncoder.encode(dto.getPassword()) : entity.getPassword())
                .role(entity.getRole())
                .build();
    }

    public ResponseEntity<UserCreditsResponse> myCredits(User currentUser) {

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado!"));

        return ResponseEntity.ok(new UserCreditsResponse(user.getCredits()));
    }

    @Transactional
    public UserCreditsResponse addCredits(UUID userId, @Valid UserCreditsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado!"));

        if (request.credits() <= 0) {
            throw new IllegalArgumentException("A quantidade de creditos deve ser positiva.");
        }

        user.setCredits(user.getCredits() + request.credits());
        userRepository.save(user);

        return new UserCreditsResponse(user.getCredits());
    }
}
