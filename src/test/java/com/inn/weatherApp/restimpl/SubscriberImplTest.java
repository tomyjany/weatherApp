package com.inn.weatherApp.restimpl;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriberImplTest {

    private final SubscriberImpl subscriberImpl = new SubscriberImpl();

    @Test
    public void youAreSubscribed_ReturnsAcceptedResponse() {
        // Act
        ResponseEntity<String> response = subscriberImpl.youAreSubscribed();

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("{\"message\":\"Congratulation, you are now subscribed!\"}", response.getBody());
    }
}