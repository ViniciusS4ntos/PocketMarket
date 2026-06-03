package com.pocketmarket.controleTeste;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ControleTeste {

    // http://localhost:8080/api/v1/ping <- faca o teste aqui
    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("ok");
    }

}
