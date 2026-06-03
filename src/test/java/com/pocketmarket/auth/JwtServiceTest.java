package com.pocketmarket.auth;

import com.pocketmarket.enums.UserRole;
import com.pocketmarket.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void generateAndValidateTokenReturnsUserEmail() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        User user = User.builder()
                .name("Ash")
                .email("ash@pm.com")
                .role(UserRole.USER)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.validateToken(token)).isEqualTo(user.getEmail());
    }
}
