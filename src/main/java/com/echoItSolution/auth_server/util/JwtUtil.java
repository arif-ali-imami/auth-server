package com.echoItSolution.auth_server.util;

import com.echoItSolution.auth_server.dto.AuthResponseDTO;
import com.echoItSolution.auth_server.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
}
