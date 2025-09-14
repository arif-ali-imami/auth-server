package com.echoItSolution.auth_server.clients;

import com.echoItSolution.auth_server.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/api/v1/account/user/create")
    UserDTO signUp(@RequestBody Map<String, Object> userRequest);
}
