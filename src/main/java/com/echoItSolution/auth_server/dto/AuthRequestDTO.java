package com.echoItSolution.auth_server.dto;

import com.echoItSolution.auth_server.entity.AuthenticationType;
import lombok.Data;

@Data
public class AuthRequestDTO {

    private String username;
    private String password;
    private String refreshToken;
    private AuthenticationType authenticationType;
}
