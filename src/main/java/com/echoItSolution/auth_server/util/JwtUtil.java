package com.echoItSolution.auth_server.util;

import ch.qos.logback.core.util.StringUtil;
import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.dto.CustomUserDetails;
import com.echoItSolution.auth_server.enums.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private SecretKey getSecretKey(){
        String secretKey = "gnr4ng458hjtg80459gh458g4580jg45rg";
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /** Extract a custom claim by name and type, e.g., ("userId", String.class). */
    public <T> T extractClaim(String token, String claimName, Class<T> type) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName, type);
    }

    private Claims extractAllClaims(String token) {
        // Will throw if token is tampered/invalid/expired (ExpiredJwtException extends JwtException)
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateJwtToken(CustomUserDetails customUserDetails){
        long localTime = System.currentTimeMillis();
        return Jwts.builder()
                .subject(customUserDetails.getUsername())
                .claim("roles", customUserDetails.getAuthorities())
                .issuedAt(new Date(localTime))
                .expiration(new Date(localTime + 1000*60*15))
                .signWith(getSecretKey())
                .compact();

    }

    public AuthResponseDTO generateResponse(String token, String refreshToken, CustomUserDetails customUserDetail){
        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(customUserDetail.getUsername())
                .expirationTime(extractClaim(token, "exp", Long.class))
                .iat(extractClaim(token, "iat", Long.class))
                .userId(customUserDetail.getUserId()).build();
    }

    public String findUsernameFromOAuth2User(OAuth2User oAuth2User, String registrationId, String providerId) {
        String email = oAuth2User.getAttribute("email");
        if(StringUtil.notNullNorEmpty(email))
            return email;
        return switch (registrationId){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("login");
            default -> providerId;
        };
    }

    public AuthProviderType getProviderType(String registrationId) {
        return switch (registrationId){
            case "google" -> AuthProviderType.GOOGLE;
            case "facebook" -> AuthProviderType.FACEBOOK;
            case "github" -> AuthProviderType.GITHUB;
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        };
    }

    public String getProviderId(String registrationId, OAuth2User oAuth2User) {
        return switch (registrationId){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
            default -> throw new IllegalStateException("Unexpected auth provider: " + registrationId);
        };
    }

    // Refresh Token (long-lived, e.g., 7 days)
    public String generateRefreshToken(CustomUserDetails userDetail) {
        return Jwts.builder()
                .subject(userDetail.getUsername())
                .claim("userId", userDetail.getUserId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token); // will throw if invalid or expired
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
