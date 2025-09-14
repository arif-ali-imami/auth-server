package com.echoItSolution.auth_server.security;

import com.echoItSolution.auth_server.clients.UserClient;
import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.dto.CustomUserDetails;
import com.echoItSolution.auth_server.dto.UserDTO;
import com.echoItSolution.auth_server.entity.User;
import com.echoItSolution.auth_server.enums.AuthProviderType;
import com.echoItSolution.auth_server.repository.UserRepository;
import com.echoItSolution.auth_server.util.JwtUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2LoginService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserClient userClient;

    public ResponseEntity<AuthResponseDTO> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // fetch providerId
        AuthProviderType providerType = jwtUtil.getProviderType(registrationId);
        String providerId = jwtUtil.getProviderId(registrationId, oAuth2User);
        User user = userRepository.findByProviderTypeAndProviderId(providerType, providerId).orElse(null);

        String email = oAuth2User.getAttribute("email");

        User emailUser = userRepository.findByUserName(email).orElse(null);
        UserDTO userDTO;
        if(user == null && emailUser == null){
            // sign up flow
            String username = jwtUtil.findUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            userDTO = signUpUser(username, providerType, providerId);
        }
        else if (user != null){
            if(StringUtils.isNotBlank(email) && !user.getUserName().equals(email)){
                user.setUserName(email);
                userRepository.save(user);
            }
            userDTO = new UserDTO(user.getId(), user.getUserName(), user.getUserRoles());
        } else {
            userDTO = new UserDTO(emailUser.getId(), emailUser.getUserName(), emailUser.getUserRoles());
        }
        CustomUserDetails customUserDetail = CustomUserDetails.builder()
                .userId(userDTO.getUserId())
                .username(userDTO.getUserName())
                .userRoles(userDTO.getUserRoles())
                .build();
        String token = jwtUtil.generateJwtToken(customUserDetail);
        String refreshToken = jwtUtil.generateRefreshToken(customUserDetail);
        AuthResponseDTO loginResponseDTO = jwtUtil.generateResponse(token, refreshToken, customUserDetail);
        return ResponseEntity.ok(loginResponseDTO);
    }

    private UserDTO signUpUser(String username, AuthProviderType providerType, String providerId) {
//        String url = "http://USER-SERVICE/api/v1/account/user/create";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userName", username);
        requestBody.put("providerType", providerType.name());
        requestBody.put("password", "12345");
        requestBody.put("providerId", providerId);
//        return restTemplate.postForObject(url, requestBody, UserDTO.class);
        return userClient.signUp(requestBody);
    }

}
