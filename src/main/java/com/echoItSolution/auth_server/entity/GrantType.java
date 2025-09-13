package com.echoItSolution.auth_server.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GrantType {
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token");

    private String grantType;
    @JsonValue
    public String getGrantType() {
        return grantType;
    }

    @JsonCreator
    public static GrantType fromValue(String value) {
        for (GrantType type : GrantType.values()) {
            if (type.getGrantType().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid grant type: " + value);
    }

}
