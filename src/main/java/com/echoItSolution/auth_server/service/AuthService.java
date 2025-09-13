package com.echoItSolution.auth_server.service;

import com.echoItSolution.auth_server.dto.AuthRequestDTO;
import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.dto.CustomUserDetails;
import com.echoItSolution.auth_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public ResponseEntity<AuthResponseDTO> login(AuthRequestDTO requestDTO) {
        CustomUserDetails customUserDetail = null;
        String token = null;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
            );
            if(authentication == null)
                throw new IllegalArgumentException("Invalid request");
            customUserDetail = (CustomUserDetails)authentication.getPrincipal();
            token = jwtUtil.generateJwtToken(customUserDetail);
        }catch (Exception e){
            throw new IllegalArgumentException("Invalid request");
        }
        return ResponseEntity.ok(jwtUtil.generateResponse(token, null, customUserDetail));
    }
}
