package com.echoItSolution.auth_server.service;

import com.echoItSolution.auth_server.dto.AuthRequestDTO;
import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.dto.CustomUserDetails;
import com.echoItSolution.auth_server.entity.GrantType;
import com.echoItSolution.auth_server.entity.User;
import com.echoItSolution.auth_server.repository.UserRepository;
import com.echoItSolution.auth_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ResponseEntity<AuthResponseDTO> login(AuthRequestDTO requestDTO) {
        if(GrantType.PASSWORD.equals(requestDTO.getGrantType()))
            return loginWithPassword(requestDTO);
        else if(GrantType.REFRESH_TOKEN.equals(requestDTO.getGrantType()))
            return refreshAccessToken(requestDTO);
        throw new IllegalArgumentException("grant_type is missing");
    }

    private ResponseEntity<AuthResponseDTO> loginWithPassword(AuthRequestDTO requestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
            );
            if(authentication == null)
                throw new IllegalArgumentException("Invalid request");
            CustomUserDetails customUserDetail = (CustomUserDetails)authentication.getPrincipal();
            String token = jwtUtil.generateJwtToken(customUserDetail);
            String refreshToken = jwtUtil.generateRefreshToken(customUserDetail);
            return ResponseEntity.ok(jwtUtil.generateResponse(token, refreshToken, customUserDetail));
        }catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid username or password");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Authentication failed");
        }
    }

    public ResponseEntity<AuthResponseDTO> refreshAccessToken(AuthRequestDTO requestDTO) {
        String refreshToken = requestDTO.getRefreshToken();
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtUtil.extractClaim(refreshToken, "sub", String.class);
        Long userId = Long.valueOf(jwtUtil.extractClaim(refreshToken, "userId", String.class));

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                                                .userId(userId)
                                                .username(username)
                                                .userRoles(user.getUserRoles())
                                                .build();

        String newAccessToken = jwtUtil.generateJwtToken(customUserDetails);

        // Reuse same refreshToken (or issue a new one if you prefer rotation)
        return ResponseEntity.ok(jwtUtil.generateResponse(newAccessToken, refreshToken, customUserDetails));
    }

}
