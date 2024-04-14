package com.inn.weatherApp.serviceImpl;


import com.inn.weatherApp.dao.UserDao;
import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.utils.WeatherUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private Map<String, String> validRequestMap;
    private Map<String, String> invalidRequestMap;


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
}
