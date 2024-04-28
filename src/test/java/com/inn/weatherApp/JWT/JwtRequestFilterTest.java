package com.inn.weatherApp.JWT;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {

    @Mock
    private CustomerDetailsService customerDetailsService;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private static final String SAMPLE_JWT = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTYxNzk5NTIwMH0.abcde12345";
    private static final String USERNAME = "username";

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldAuthenticateUserWhenTokenIsValid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(SAMPLE_JWT);
        when(jwtUtil.extractUsername(SAMPLE_JWT.substring(7))).thenReturn(USERNAME);
        UserDetails userDetails = org.mockito.Mockito.mock(UserDetails.class);
        when(customerDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(jwtUtil.validateToken(SAMPLE_JWT.substring(7), userDetails)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assert(SecurityContextHolder.getContext().getAuthentication() != null);
    }

    @Test
    public void shouldContinueFilterChainWhenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assert(SecurityContextHolder.getContext().getAuthentication() == null);
    }
}