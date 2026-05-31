package com.pocketmarket.security;

import com.pocketmarket.auth.JwtService;
import com.pocketmarket.enums.UserRole;
import com.pocketmarket.user.User;
import com.pocketmarket.user.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterContinuesWithoutAuthenticationWhenHeaderIsMissing() throws Exception {
        SecurityFilter filter = new SecurityFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void doFilterContinuesWithoutAuthenticationWhenHeaderIsNotBearer() throws Exception {
        SecurityFilter filter = new SecurityFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void doFilterAuthenticatesUserWhenTokenIsValid() throws Exception {
        SecurityFilter filter = new SecurityFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = User.builder()
                .email("ash@pm.com")
                .password("encoded")
                .role(UserRole.USER)
                .build();

        when(jwtService.validateToken("jwt")).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isSameAs(user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterContinuesWithoutAuthenticationWhenTokenHasNoEmail() throws Exception {
        SecurityFilter filter = new SecurityFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("jwt")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterThrowsWhenTokenUserDoesNotExist() {
        SecurityFilter filter = new SecurityFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("jwt")).thenReturn("missing@pm.com");
        when(userRepository.findByEmail("missing@pm.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Id do usuario  nao encontrado!");
    }
}
