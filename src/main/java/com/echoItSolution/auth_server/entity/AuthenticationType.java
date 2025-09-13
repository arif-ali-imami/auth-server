package com.echoItSolution.auth_server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthenticationType {
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token");

    private String authType;
}
