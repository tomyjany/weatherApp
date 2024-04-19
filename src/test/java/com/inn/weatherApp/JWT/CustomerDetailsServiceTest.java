package com.inn.weatherApp.JWT;

import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomerDetailsService service;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUser_password("password123");
    }

    @Test
    public void loadUserByUsername_userExists_returnsUserDetails() {
        // Arrange
        when(userDao.findByEmail("test@example.com")).thenReturn(user);

        // Act
        UserDetails result = service.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty()); // Expect no roles to be set
        verify(userDao).findByEmail("test@example.com");
    }

    @Test
    public void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        // Arrange
        when(userDao.findByEmail("unknown@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("unknown@example.com");
        });
    }
}
