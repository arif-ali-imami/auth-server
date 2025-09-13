package com.echoItSolution.auth_server.controller;

import com.echoItSolution.auth_server.dto.AuthRequestDTO;
import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO requestDTO
            ){
        return authService.login(requestDTO);
    }
}
