package com.pocketmarket.user;

import com.pocketmarket.user.dtos.in.UserDTORequest;
import com.pocketmarket.user.dtos.out.UserDTOResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> buscarUsuarioPorId(@PathVariable Integer  id){
        return ResponseEntity.ok(
                mapper.map(userService.buscarPerfilPorId(id), UserDTOResponse.class)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTOResponse> atualizarPerfil(@PathVariable Integer id, @RequestBody UserDTORequest user){
        User entity = mapper.map(user, User.class);
        return ResponseEntity.ok(
                mapper.map(userService.atualizarPerfil(id, entity), UserDTOResponse.class)
        );
    }

}
