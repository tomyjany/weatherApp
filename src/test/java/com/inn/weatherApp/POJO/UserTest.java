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
    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(2);
        user.setFirst_name("Jane");
        user.setLast_name("Doe");
        user.setEmail("jane.doe@example.com");
        user.setUser_password("anotherSecurePassword");
        user.setSubscription(false);

        assertEquals(2, user.getId());
        assertEquals("Jane", user.getFirst_name());
        assertEquals("Doe", user.getLast_name());
        assertEquals("jane.doe@example.com", user.getEmail());
        assertEquals("anotherSecurePassword", user.getUser_password());
        assertFalse(user.isSubscription());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1);
        user1.setFirst_name("John");
        user1.setLast_name("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setUser_password("securePassword");
        user1.setSubscription(true);

        User user2 = new User();
        user2.setId(1);
        user2.setFirst_name("John");
        user2.setLast_name("Doe");
        user2.setEmail("john.doe@example.com");
        user2.setUser_password("securePassword");
        user2.setSubscription(true);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john.doe@example.com");
        user.setUser_password("securePassword");
        user.setSubscription(true);

        String expectedString = "User(id=1, first_name=John, last_name=Doe, email=john.doe@example.com, user_password=securePassword, subscription=true)";
        assertEquals(expectedString, user.toString());
    }
}

