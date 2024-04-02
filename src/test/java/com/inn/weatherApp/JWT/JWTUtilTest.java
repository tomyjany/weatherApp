package com.inn.weatherApp.JWT;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JWTUtilTest {

    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
    }

    @Test
    void testGenerateTokenContainsUsernameAndExpiration() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
        assertThat(jwtUtil.extractExpiration(token)).isAfterOrEqualTo(new Date());
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void testExtractExpiration() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username, "ROLE_USER");

        Date expiration = jwtUtil.extractExpiration(token);

        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfterOrEqualTo(new Date());
    }

    @Test
    void testValidateTokenWithValidUser() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);
        UserDetails userDetails = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority(role)));

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateTokenWithInvalidUser() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);
        UserDetails userDetails = new User("wrongUser", "", Collections.singletonList(new SimpleGrantedAuthority(role)));

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        // You might need to adjust JWTUtil to make the expiration time configurable for this test
        // Or mock the part where it gets the current time
        String username = "expiredUser";
        String role = "ROLE_USER";
        // Assuming a method to generate an expired token for testing
        String token = jwtUtil.generateToken(username, role);
        // Manually set the expiration of the token to the past
        jwtUtil = new JWTUtil() {
            @Override
            public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
                return claimsResolver.apply(Jwts.claims().setExpiration(new Date(System.currentTimeMillis() - 1000)));
            }
        };

        UserDetails userDetails = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority(role)));

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isFalse();
    }

    // Add more tests as necessary for complete coverage
}

