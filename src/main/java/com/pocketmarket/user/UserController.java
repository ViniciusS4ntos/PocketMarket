package com.pocketmarket.user;

import com.pocketmarket.user.dtos.in.UserDTORequest;
import com.pocketmarket.user.dtos.out.UserCreditsResponse;
import com.pocketmarket.user.dtos.out.UserDTOResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> buscarUsuarioPorId(@PathVariable UUID id){
        return ResponseEntity.ok(
                mapper.map(userService.buscarPerfilPorId(id), UserDTOResponse.class)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTOResponse> atualizarPerfil(@PathVariable UUID id, @RequestBody UserDTORequest user){
        User entity = mapper.map(user, User.class);
        return ResponseEntity.ok(
                mapper.map(userService.atualizarPerfil(id, entity), UserDTOResponse.class)
        );
    }

    @GetMapping("/me/credits")
    public ResponseEntity<UserCreditsResponse> myCredits(@AuthenticationPrincipal User currentUser){
        return userService.myCredits(currentUser);
    }
}
