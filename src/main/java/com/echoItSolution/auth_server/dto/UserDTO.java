package com.echoItSolution.auth_server.dto;

import com.echoItSolution.auth_server.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long userId;
    private String userName;
    private Set<Role> userRoles;

}
