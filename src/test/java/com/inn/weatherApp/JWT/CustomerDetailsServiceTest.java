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
    private CustomerDetailsService customerDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUser_password("password");
        user.setSubscription(true); // Assume the user is subscribed
    }

    @Test
    void whenUserFoundAndSubscribed_thenAssignsRoleSubscribed() {
        when(userDao.findByEmail("test@example.com")).thenReturn(user);

        UserDetails userDetails = customerDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUBSCRIBED")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_UNSUBSCRIBED")));
    }

    @Test
    void whenUserFoundAndNotSubscribed_thenAssignsRoleUnsubscribed() {
        user.setSubscription(false);
        when(userDao.findByEmail("test@example.com")).thenReturn(user);

        UserDetails userDetails = customerDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_UNSUBSCRIBED")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUBSCRIBED")));
    }

    @Test
    void whenUserNotFound_thenThrowsUsernameNotFoundException() {
        when(userDao.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customerDetailsService.loadUserByUsername("unknown@example.com");
        });
    }

    @Test
    void testUserDetailGetter() {
        user.setEmail("getter@example.com");
        when(userDao.findByEmail("getter@example.com")).thenReturn(user);

        customerDetailsService.loadUserByUsername("getter@example.com");
        assertEquals("getter@example.com", customerDetailsService.getUserDetail().getEmail());
    }
}
