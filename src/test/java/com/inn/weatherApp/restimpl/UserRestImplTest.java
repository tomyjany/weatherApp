package com.inn.weatherApp.restimpl;

import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.utils.WeatherUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRestImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestImpl userRest;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, String> requestMap;

    @BeforeEach
    public void setUp() {
        requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
    }

    @Test
    public void singUp_ServiceReturnsSuccess_ReturnsSameResponse() {
        // Arrange
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("User registered successfully", HttpStatus.OK);
        when(userService.signUp(requestMap)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = userRest.singUp(requestMap);

        // Assert
        assertEquals(expectedResponse, response);
        verify(userService).signUp(requestMap);
    }

    @Test
    public void singUp_ServiceThrowsException_ReturnsErrorResponse() throws Exception {
        // Arrange
        when(userService.signUp(requestMap)).thenThrow(new RuntimeException("Service failure"));

        // Act
        ResponseEntity<String> response = userRest.singUp(requestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals("something went wrong", responseMap.get("message"));
    }
    @Test
    public void signIn_MissingCredentials_ReturnsBadRequest() {
        // Arrange
        requestMap.remove("user_password");

        // Act
        ResponseEntity<String> response = userRest.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Email and password are required"));
    }

    @Test
    public void signIn_ValidCredentials_ReturnsSuccessResponse() {
        // Arrange
        Map<String, String> validCredentials = new HashMap<>();
        validCredentials.put("email", "test@example.com");
        validCredentials.put("user_password", "password123");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Login successful");
        when(userService.signIn(validCredentials)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = userRest.signIn(validCredentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
        verify(userService).signIn(validCredentials);
    }

    @Test
    public void testUser_ReturnsExpectedMessage() {
        // Act
        ResponseEntity<String> response = userRest.testUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Hello this is USER endpoint!\"}", response.getBody());
    }
}

