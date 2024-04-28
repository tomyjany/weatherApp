package com.inn.weatherApp.serviceImpl;


import com.inn.weatherApp.JWT.CustomerDetailsService;
import com.inn.weatherApp.JWT.JWTUtil;
import com.inn.weatherApp.dao.UserDao;
import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.utils.WeatherUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetails userDetails;
    @Mock
    private CustomerDetailsService customerDetailsService;
    @Mock
    private JWTUtil jwtUtil;
    @InjectMocks
    private UserServiceImpl userService;

    private Map<String, String> validRequestMap;
    private Map<String, String> invalidRequestMap;
    private Map<String, String> requestMap;


    @BeforeEach
    public void setUp() {
        // Valid request map
        validRequestMap = new HashMap<>();
        validRequestMap.put("first_name", "John");
        validRequestMap.put("last_name", "Doe");
        validRequestMap.put("email", "john.doe@example.com");
        validRequestMap.put("user_password", "123456");

        // Invalid request map (missing 'email' key)
        invalidRequestMap = new HashMap<>();
        invalidRequestMap.put("first_name", "John");
        invalidRequestMap.put("last_name", "Doe");
        invalidRequestMap.put("user_password", "123456");

        requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        requestMap.put("user_password", "password123");
        userDetails = org.mockito.Mockito.mock(UserDetails.class);

    }
    @Test
    public void signUp_ValidRequest_NewUser_ReturnsAccepted() {
        // Arrange
        when(userDao.findByEmail("john.doe@example.com")).thenReturn(null);
        when(userDao.save(any(User.class))).thenReturn(null);

        // Act
        ResponseEntity<String> response = userService.signUp(validRequestMap);


        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("{\"message\":\"Register success\"}", response.getBody());
        verify(userDao).save(any(User.class));
    }

    @Test
    public void signUp_ValidRequest_UserExists_ReturnsBadRequest() {
        // Arrange
        when(userDao.findByEmail("john.doe@example.com")).thenReturn(new User());

        // Act
        ResponseEntity<String> response = userService.signUp(validRequestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"email is already registered\"}", response.getBody());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    public void signUp_InvalidRequest_ReturnsBadRequest() {
        // Act
        ResponseEntity<String> response = userService.signUp(invalidRequestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"WRONG Credentials\"}", response.getBody());
    }

    @Test
    public void signUp_ExceptionDuringProcess_ReturnsInternalServerError() {
        // Arrange
        when(userDao.findByEmail("john.doe@example.com")).thenThrow(new RuntimeException("Database failure"));

        // Act
        ResponseEntity<String> response = userService.signUp(validRequestMap);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Something went wrong\"}", response.getBody());
    }
    @Test
    public void signIn_Successful_ReturnsToken() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john.doe@example.com");
        credentials.put("user_password", "123456");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("john.doe@example.com");
        when(mockUserDetails.getPassword()).thenReturn("hashedpassword");

        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        // Use doReturn for setting up the return value of getAuthorities
        doReturn(grantedAuthorities).when(mockUserDetails).getAuthorities();

        when(customerDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("123456", mockUserDetails.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token");

        // Act
        ResponseEntity<String> response = userService.signIn(credentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody());
    }

    @Test
    public void signIn_InvalidPassword_ReturnsUnauthorized() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john.doe@example.com");
        credentials.put("user_password", "wrongpassword");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn("hashedpassword");
        when(customerDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("wrongpassword", mockUserDetails.getPassword())).thenReturn(false);

        // Act
        ResponseEntity<String> response = userService.signIn(credentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void signIn_UserNotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "unknown@example.com");
        credentials.put("user_password", "123456");

        when(customerDetailsService.loadUserByUsername("unknown@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        ResponseEntity<String> response = userService.signIn(credentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void shouldHandleDataAccessException() {
        when(customerDetailsService.loadUserByUsername("test@example.com"))
                .thenThrow(new DataAccessException("Database not reachable") {});

        ResponseEntity<String> response = userService.signIn(requestMap);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Service unavailable", response.getBody());
    }

    @Test
    public void shouldHandleUnexpectedException() {
        when(customerDetailsService.loadUserByUsername("test@example.com"))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = userService.signIn(requestMap);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

}
