package com.pocketmarket.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // visualizar perfil
    public User buscarPerfilPorId(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil nao encontrado!"));
    }

}
