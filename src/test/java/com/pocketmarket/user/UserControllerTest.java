package com.pocketmarket.user;

import com.pocketmarket.user.dtos.in.UserCreditsRequest;
import com.pocketmarket.user.dtos.in.UserDTORequest;
import com.pocketmarket.user.dtos.out.UserCreditsResponse;
import com.pocketmarket.user.dtos.out.UserDTOResponse;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void buscarUsuarioPorIdDelegatesToServiceAndMapper() {
        UserController controller = controller();
        UserService userService = mock(UserService.class);
        ModelMapper mapper = mock(ModelMapper.class);
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "mapper", mapper);
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).name("Ash").email("ash@pm.com").build();
        UserDTOResponse dto = new UserDTOResponse();

        when(userService.buscarPerfilPorId(id)).thenReturn(user);
        when(mapper.map(user, UserDTOResponse.class)).thenReturn(dto);

        assertThat(controller.buscarUsuarioPorId(id).getBody()).isSameAs(dto);
    }

    @Test
    void atualizarPerfilDelegatesToServiceAndMapper() {
        UserController controller = controller();
        UserService userService = mock(UserService.class);
        ModelMapper mapper = mock(ModelMapper.class);
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "mapper", mapper);
        UUID id = UUID.randomUUID();
        UserDTORequest request = new UserDTORequest();
        User mappedRequest = User.builder().name("Misty").build();
        User updated = User.builder().id(id).name("Misty").build();
        UserDTOResponse response = new UserDTOResponse();

        when(mapper.map(request, User.class)).thenReturn(mappedRequest);
        when(userService.atualizarPerfil(id, mappedRequest)).thenReturn(updated);
        when(mapper.map(updated, UserDTOResponse.class)).thenReturn(response);

        assertThat(controller.atualizarPerfil(id, request).getBody()).isSameAs(response);
    }

    @Test
    void myCreditsDelegatesToService() {
        UserController controller = controller();
        UserService userService = mock(UserService.class);
        ReflectionTestUtils.setField(controller, "userService", userService);
        User user = User.builder().id(UUID.randomUUID()).build();
        ResponseEntity<UserCreditsResponse> response = ResponseEntity.ok(new UserCreditsResponse(10L));
        when(userService.myCredits(user)).thenReturn(response);

        assertThat(controller.myCredits(user)).isSameAs(response);
    }

    @Test
    void addCreditsDelegatesToService() {
        UserController controller = controller();
        UserService userService = mock(UserService.class);
        ReflectionTestUtils.setField(controller, "userService", userService);
        UUID userId = UUID.randomUUID();
        UserCreditsRequest request = new UserCreditsRequest(50L);
        UserCreditsResponse response = new UserCreditsResponse(150L);
        when(userService.addCredits(userId, request)).thenReturn(response);

        assertThat(controller.addCredits(userId, request)).isSameAs(response);
    }

    private UserController controller() {
        return new UserController();
    }
}
