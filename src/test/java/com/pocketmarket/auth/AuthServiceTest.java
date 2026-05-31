package com.pocketmarket.auth;

import com.pocketmarket.auth.dto.LoginRequest;
import com.pocketmarket.auth.dto.LoginResponse;
import com.pocketmarket.auth.dto.RegisterRequest;
import com.pocketmarket.enums.UserRole;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserAndReturnsBearerToken() {
        RegisterRequest request = new RegisterRequest("Ash", "ash@pm.com", "secret");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt");

        LoginResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("Bearer jwt");
        assertThat(response.email()).isEqualTo(request.email());
        assertThat(response.name()).isEqualTo(request.name());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerRejectsDuplicatedEmail() {
        RegisterRequest request = new RegisterRequest("Ash", "ash@pm.com", "secret");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já cadastrado");
    }

    @Test
    void loginReturnsTokenWhenPasswordMatches() {
        LoginRequest request = new LoginRequest("ash@pm.com", "secret");
        User user = User.builder()
                .name("Ash")
                .email(request.email())
                .password("encoded")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt");

        LoginResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt");
        assertThat(response.email()).isEqualTo(user.getEmail());
        assertThat(response.name()).isEqualTo(user.getName());
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = new LoginRequest("ash@pm.com", "wrong");
        User user = User.builder().email(request.email()).password("encoded").build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Senha incorreta");
    }

    @Test
    void loginRejectsUnknownUser() {
        LoginRequest request = new LoginRequest("missing@pm.com", "secret");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");
    }
}
