package com.echoItSolution.auth_server.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    BOOKING_VIEW("booing:view"),
    BOOKING_CREATE("booking:create"),
    BOOKING_DELETE("booking:delete");

    private final String permission;
}
