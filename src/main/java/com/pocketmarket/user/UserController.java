package com.pocketmarket.user;

import com.pocketmarket.user.dtos.out.UserDTOResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> buscarUsuarioPorId(@PathVariable("id") Integer  id){
        return ResponseEntity.ok(
                mapper.map(userService.buscarPerfilPorId(id), UserDTOResponse.class)
        );
    }

}
