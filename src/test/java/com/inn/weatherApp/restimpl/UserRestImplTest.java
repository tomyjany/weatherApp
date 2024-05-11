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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;


import java.lang.reflect.Method;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRestImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestImpl userRest;
    private Method validateSignUpMapMethod;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, String> requestMap;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        validateSignUpMapMethod = UserRestImpl.class.getDeclaredMethod("validateSignUpMap", Map.class);
        validateSignUpMapMethod.setAccessible(true);

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
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals("something went wrong", responseMap.get("message"));
    }

    @Test
    public void testUser_ReturnsExpectedMessage() {
        // Act
        ResponseEntity<String> response = userRest.testUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Hello this is USER endpoint!\"}", response.getBody());
    }
    @Test
    public void validateSignUpMap_ValidInput_ReturnsTrue() throws Exception {
        // Arrange
        Map<String, String> validMap = new HashMap<>();
        validMap.put("first_name", "Jeremy");
        validMap.put("last_name", "Clarkson");
        validMap.put("email", "normal_user@gmail.com");
        validMap.put("user_password", "heslo123");

        // Act
        boolean result = (Boolean) validateSignUpMapMethod.invoke(userRest, validMap);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validateSignUpMap_InvalidInput_ReturnsFalse() throws Exception {
        // Arrange
        Map<String, String> invalidMap = new HashMap<>();
        invalidMap.put("first_name", "Jo"); // Too short
        invalidMap.put("last_name", "Clarkson");
        invalidMap.put("email", "normal_user@gmail.com");
        invalidMap.put("user_password", "heslo123");

        // Act
        boolean result = (Boolean) validateSignUpMapMethod.invoke(userRest, invalidMap);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateSignUpMap_EmptyField_ReturnsFalse() throws Exception {
        // Arrange
        Map<String, String> emptyFieldMap = new HashMap<>();
        emptyFieldMap.put("first_name", ""); // Empty
        emptyFieldMap.put("last_name", "Clarkson");
        emptyFieldMap.put("email", "normal_user@gmail.com");
        emptyFieldMap.put("user_password", "heslo123");

        // Act
        boolean result = (Boolean) validateSignUpMapMethod.invoke(userRest, emptyFieldMap);

        // Assert
        assertFalse(result);
    }
    @Test
    public void pay_ValidToken_ReturnsSuccessResponse() {
        // Arrange
        String validToken = "validToken";

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Payment successful");
        when(userService.pay(validToken)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = userRest.pay(validToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment successful", response.getBody());
        verify(userService).pay(validToken);
    }

    @Test
    public void singUp_InvalidRequestMap_ReturnsBadRequest() {
        // Arrange
        Map<String, String> invalidRequestMap = new HashMap<>();
        invalidRequestMap.put("email", "te"); // Too short to be valid

        // Act
        ResponseEntity<String> response = userRest.singUp(invalidRequestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Wrong Credentials"));
    }

    @Test
    public void signIn_MissingCredentials_ReturnsBadRequest() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        // user_password key is missing

        // Act
        ResponseEntity<Map<String, String>> response = userRest.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email and password are required", response.getBody().get("error"));
    }
    @Test
    public void signIn_ValidCredentials_ReturnsSuccessResponse() {
        // Arrange
        Map<String, String> validCredentials = new HashMap<>();
        validCredentials.put("email", "test@example.com");
        validCredentials.put("user_password", "password123");

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Login successful");

        when(userService.signIn(validCredentials)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<Map<String, String>> response = userRest.signIn(validCredentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).signIn(validCredentials);
    }

    @Test
    public void signIn_InvalidCredentials_ReturnsUnauthorizedResponse() {
        // Arrange
        Map<String, String> invalidCredentials = new HashMap<>();
        invalidCredentials.put("email", "test@example.com");
        invalidCredentials.put("user_password", "wrongpassword");

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("error", "Invalid credentials");

        when(userService.signIn(invalidCredentials)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(expectedResponse));

        // Act
        ResponseEntity<Map<String, String>> response = userRest.signIn(invalidCredentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).signIn(invalidCredentials);
    }

    @Test
    public void addFavoriteCity_ValidCity_ReturnsSuccessResponse() {
        // Arrange
        String city = "London";
        String email = "test@example.com";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("City added to favorites");

        when(userService.addFavoriteCity(email, city)).thenReturn(expectedResponse);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new User(email, "password", new ArrayList<>()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<String> response = userRest.addFavoriteCity(city);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
        verify(userService).addFavoriteCity(email, city);
    }

    @Test
    public void getFavoriteCities_ValidEmail_ReturnsFavoriteCities() {
        // Arrange
        String email = "test@example.com";
        List<String> cities = Arrays.asList("London", "Paris");
        ResponseEntity<List<String>> expectedResponse = ResponseEntity.ok(cities);

        when(userService.getFavoriteCities(email)).thenReturn(expectedResponse);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new User(email, "password", new ArrayList<>()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<List<String>> response = userRest.getFavoriteCities();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
        verify(userService).getFavoriteCities(email);
    }
    @Test
    public void getEmailFromSecurityContext_NonUserDetailsPrincipal_ReturnsPrincipalToString() throws Exception {
        // Arrange
        String principalString = "testPrincipal";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalString);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Use reflection to access the private method
        Method method = UserRestImpl.class.getDeclaredMethod("getEmailFromSecurityContext");
        method.setAccessible(true);

        // Act
        String returnedPrincipal = (String) method.invoke(userRest);

        // Assert
        assertEquals(principalString, returnedPrincipal);
    }

}

