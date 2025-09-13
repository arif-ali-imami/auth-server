package com.echoItSolution.auth_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthResponseDTO {

    private Long userId;
    private String username;
    private String token;
    private String refreshToken;
    private Long expirationTime;
    private Long iat;
}
