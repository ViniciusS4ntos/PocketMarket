package com.pocketmarket.user;

import com.pocketmarket.enums.UserRole;
import com.pocketmarket.user.dtos.in.UserCreditsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void buscarPerfilPorIdReturnsUser() {
        User user = user();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThat(userService.buscarPerfilPorId(user.getId())).isSameAs(user);
    }

    @Test
    void buscarPerfilPorIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.buscarPerfilPorId(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Perfil nao encontrado!");
    }

    @Test
    void atualizarPerfilMergesAndSavesUser() {
        User current = user();
        User update = User.builder().name("Misty").email("misty@pm.com").password("new").build();
        when(userRepository.findById(current.getId())).thenReturn(Optional.of(current));
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User response = userService.atualizarPerfil(current.getId(), update);

        assertThat(response.getId()).isEqualTo(current.getId());
        assertThat(response.getName()).isEqualTo("Misty");
        assertThat(response.getEmail()).isEqualTo("misty@pm.com");
        assertThat(response.getPassword()).isEqualTo("encoded-new");
    }

    @Test
    void atualizarPerfilThrowsWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.atualizarPerfil(id, User.builder().build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");
    }

    @Test
    void atualizarUsuarioKeepsExistingValuesWhenDtoFieldsAreNull() {
        User current = user();
        User response = userService.atualizarUsuario(current, User.builder().build());

        assertThat(response.getName()).isEqualTo(current.getName());
        assertThat(response.getEmail()).isEqualTo(current.getEmail());
        assertThat(response.getPassword()).isEqualTo(current.getPassword());
    }

    @Test
    void myCreditsReturnsCurrentCredits() {
        User user = user();
        user.setCredits(150L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.myCredits(user);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void myCreditsThrowsWhenUserDoesNotExist() {
        User user = user();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.myCredits(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");
    }

    @Test
    void addCreditsAddsToCurrentBalance() {
        User user = user();
        user.setCredits(100L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var response = userService.addCredits(user.getId(), new UserCreditsRequest(50L));

        assertThat(response.credits()).isEqualTo(150L);
    }

    @Test
    void addCreditsThrowsWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addCredits(id, new UserCreditsRequest(50L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario nao encontrado!");
    }

    private User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Ash")
                .email("ash@pm.com")
                .password("encoded")
                .role(UserRole.USER)
                .credits(0L)
                .build();
    }
}
