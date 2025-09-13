package com.echoItSolution.auth_server.repository;

import com.echoItSolution.auth_server.entity.User;
import com.echoItSolution.auth_server.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String username);

    Optional<User> findByProviderTypeAndProviderId(AuthProviderType providerType, String providerId);

}