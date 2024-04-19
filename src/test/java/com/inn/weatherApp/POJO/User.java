package com.inn.weatherApp.POJO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john.doe@example.com");
        user.setUser_password("securePassword");
        user.setSubscription(true);
    }

    @Test
    void testGetId() {
        assertEquals(1, user.getId());
    }

    @Test
    void testGetFirstName() {
        assertEquals("John", user.getFirst_name());
    }

    @Test
    void testGetLastName() {
        assertEquals("Doe", user.getLast_name());
    }

    @Test
    void testGetEmail() {
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testGetUserPassword() {
        assertEquals("securePassword", user.getUser_password());
    }

    @Test
    void testIsSubscription() {
        assertTrue(user.isSubscription());
    }
}
