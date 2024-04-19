package com.inn.weatherApp.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class WeatherUtilityTest {

    @Test
    void getResponse_ReturnsCorrectJsonMessageAndStatus() {
        // Define test cases with expected messages and statuses
        String[] messages = {"Success", "Error occurred"};
        HttpStatus[] statuses = {HttpStatus.OK, HttpStatus.BAD_REQUEST};

        // Test each message with its corresponding status
        for (int i = 0; i < messages.length; i++) {
            ResponseEntity<String> response = WeatherUtility.getResponse(messages[i], statuses[i]);

            // Assert that the response status is correct
            assertEquals(statuses[i], response.getStatusCode(),
                    "Expected status " + statuses[i] + " but got " + response.getStatusCode());

            // Assert that the response body contains the correct message in JSON format
            assertEquals("{\"message\":\"" + messages[i] + "\"}", response.getBody(),
                    "Expected message to be '" + messages[i] + "' but was '" + response.getBody() + "'");
        }
    }
}
