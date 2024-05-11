package com.inn.weatherApp.serviceImpl;


import com.inn.weatherApp.JWT.CustomerDetailsService;
import com.inn.weatherApp.JWT.JWTUtil;
import com.inn.weatherApp.POJO.FavoriteCity;
import com.inn.weatherApp.dao.FavoriteCityDao;
import com.inn.weatherApp.dao.UserDao;
import com.inn.weatherApp.POJO.User;
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
    private FavoriteCityDao favoriteCityDao;
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
    public void pay_ValidToken_SubscriptionActivated_ReturnsOk() {
        // Arrange
        String validToken = "validToken";
        User user = new User();
        user.setEmail("john.doe@example.com");

        when(jwtUtil.extractUsername(validToken)).thenReturn("john.doe@example.com");
        when(userDao.findByEmail("john.doe@example.com")).thenReturn(user);

        // Act
        ResponseEntity<String> response = userService.pay(validToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"Payment successful, subscription activated\"}", response.getBody());
        assertTrue(user.isSubscription());
        verify(userDao).save(user);
    }

    @Test
    public void pay_ValidToken_UserNotFound_ReturnsBadRequest() {
        // Arrange
        String validToken = "validToken";

        when(jwtUtil.extractUsername(validToken)).thenReturn("john.doe@example.com");
        when(userDao.findByEmail("john.doe@example.com")).thenReturn(null);

        // Act
        ResponseEntity<String> response = userService.pay(validToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"User not found\"}", response.getBody());
    }

    @Test
    public void pay_ExceptionDuringProcess_ReturnsInternalServerError() {
        // Arrange
        String validToken = "validToken";

        when(jwtUtil.extractUsername(validToken)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<String> response = userService.pay(validToken);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Something went wrong\"}", response.getBody());
    }
    @Test
    public void validateApiKey_ValidApiKey_ReturnsTrue() {
        // Arrange
        String apiKey = "validApiKey";
        when(userDao.findByApiKey(apiKey)).thenReturn(new User());

        // Act
        boolean result = userService.validateApiKey(apiKey);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validateApiKey_InvalidApiKey_ReturnsFalse() {
        // Arrange
        String apiKey = "invalidApiKey";
        when(userDao.findByApiKey(apiKey)).thenReturn(null);

        // Act
        boolean result = userService.validateApiKey(apiKey);

        // Assert
        assertFalse(result);
    }

    @Test
    public void findUserByEmailAddress_UserExists_ReturnsUserId() {
        // Arrange
        String email = "john.doe@example.com";
        User user = new User();
        user.setId(1);
        when(userDao.findByEmail(email)).thenReturn(user);

        // Act
        Integer result = userService.findUserByEmailAddress(email);

        // Assert
        assertEquals(user.getId(), result);
    }

    @Test
    public void findUserByEmailAddress_UserDoesNotExist_ReturnsNull() {
        // Arrange
        String email = "john.doe@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act
        Integer result = userService.findUserByEmailAddress(email);

        // Assert
        assertNull(result);
    }

    @Test
    public void addFavoriteCity_UserExists_CityNotInFavorites_ReturnsOk() {
        // Arrange
        String email = "john.doe@example.com";
        String city = "London";
        User user = new User();
        user.setFavoriteCities(new ArrayList<>());
        when(userDao.findByEmail(email)).thenReturn(user);

        // Act
        ResponseEntity<String> response = userService.addFavoriteCity(email, city);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"City added to favorite cities\"}", response.getBody());
    }

    @Test
    public void addFavoriteCity_UserExists_CityInFavorites_ReturnsBadRequest() {
        // Arrange
        String email = "john.doe@example.com";
        String city = "London";
        User user = new User();
        FavoriteCity favoriteCity = new FavoriteCity();
        favoriteCity.setCityName(city);
        user.setFavoriteCities(Collections.singletonList(favoriteCity));
        when(userDao.findByEmail(email)).thenReturn(user);

        // Act
        ResponseEntity<String> response = userService.addFavoriteCity(email, city);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"City is already in the list of favorite cities\"}", response.getBody());
    }

    @Test
    public void addFavoriteCity_UserDoesNotExist_ReturnsBadRequest() {
        // Arrange
        String email = "john.doe@example.com";
        String city = "London";
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act
        ResponseEntity<String> response = userService.addFavoriteCity(email, city);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\":\"User not found\"}", response.getBody());
    }

    @Test
    public void getFavoriteCities_UserExists_ReturnsFavoriteCities() {
        // Arrange
        String email = "john.doe@example.com";
        User user = new User();
        FavoriteCity favoriteCity1 = new FavoriteCity();
        favoriteCity1.setCityName("London");
        FavoriteCity favoriteCity2 = new FavoriteCity();
        favoriteCity2.setCityName("Paris");
        user.setFavoriteCities(Arrays.asList(favoriteCity1, favoriteCity2));
        when(userDao.findByEmail(email)).thenReturn(user);

        // Act
        ResponseEntity<List<String>> response = userService.getFavoriteCities(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Arrays.asList("London", "Paris"), response.getBody());
    }

    @Test
    public void getFavoriteCities_UserDoesNotExist_ReturnsNotFound() {
        // Arrange
        String email = "john.doe@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // Act
        ResponseEntity<List<String>> response = userService.getFavoriteCities(email);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void signIn_ValidCredentials_ReturnsOk() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        requestMap.put("user_password", "password123");

        // Create a TestingAuthenticationToken with the role
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_UNSUBSCRIBED");
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);
        UserDetails userDetailsMock = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "password123",
                authorities
        );

        User user = new User();
        user.setApi_key("validApiKey");

        when(customerDetailsService.loadUserByUsername(eq("test@example.com"))).thenReturn(userDetailsMock);
        when(passwordEncoder.matches(eq("password123"), eq("password123"))).thenReturn(true);
        when(userDao.findByEmail(eq("test@example.com"))).thenReturn(user);
        when(jwtUtil.generateToken(eq("test@example.com"), anyString())).thenReturn("validToken");

        // Act
        ResponseEntity<Map<String, String>> response = userService.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("validToken", response.getBody().get("token"));
        assertEquals("validApiKey", response.getBody().get("apiKey"));
    }

    @Test
    public void signIn_InvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        requestMap.put("user_password", "wrongPassword");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn("password123");

        when(customerDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("wrongPassword", "password123")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, String>> response = userService.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("error"));
    }

    @Test
    public void signIn_UserNotFound_ReturnsUnauthorized() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "nonexistent@example.com");
        requestMap.put("user_password", "password123");

        when(customerDetailsService.loadUserByUsername("nonexistent@example.com")).thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        ResponseEntity<Map<String, String>> response = userService.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found", response.getBody().get("error"));
    }

    @Test
    public void signIn_DataAccessException_ReturnsInternalServerError() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        requestMap.put("user_password", "password123");

        when(customerDetailsService.loadUserByUsername("test@example.com")).thenThrow(new DataAccessException("Database access issue") {});

        // Act
        ResponseEntity<Map<String, String>> response = userService.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Service unavailable", response.getBody().get("error"));
    }

    @Test
    public void signIn_UnexpectedException_ReturnsInternalServerError() {
        // Arrange
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "test@example.com");
        requestMap.put("user_password", "password123");

        when(customerDetailsService.loadUserByUsername("test@example.com")).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<Map<String, String>> response = userService.signIn(requestMap);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().get("error"));
    }

}
