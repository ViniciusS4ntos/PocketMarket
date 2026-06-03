package com.pocketmarket.auth;

import com.pocketmarket.auth.dto.*;
import com.pocketmarket.enums.UserRole;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // criar user
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .role(UserRole.USER)
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        var token = "Bearer " + jwtService.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getName());
    }

    // logar user
    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }
        var token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getName());
    }

    public User getAuthenticatedUser() {

        String email = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication(),
                "Usuário não autenticado"
        ).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("Usuário não encontrado")
                );
    }
}