package com.echoItSolution.auth_server.service;

import com.echoItSolution.auth_server.dto.CustomUserDetails;
import com.echoItSolution.auth_server.entity.User;
import com.echoItSolution.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow();
        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(username).password(user.getPassword()).userRoles(user.getUserRoles())
                .build();
    }
}
