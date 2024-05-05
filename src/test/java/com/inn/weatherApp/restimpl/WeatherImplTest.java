package com.inn.weatherApp.restimpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class WeatherImplTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private WeatherImpl weatherImpl;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCurrentWeather_ValidApiKey_ReturnsWeatherData() {
        // Arrange
        String city = "ValidCity";
        String apiKey = "ValidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(true);
        when(weatherService.getCurrentWeather(city)).thenReturn(ResponseEntity.ok(Map.of("data", "weather data")));

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getCurrentWeather(city, apiKey);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("weather data", response.getBody().get("data"));
    }

    @Test
    public void getCurrentWeather_InvalidApiKey_ReturnsError() {
        // Arrange
        String city = "ValidCity";
        String apiKey = "InvalidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getCurrentWeather(city, apiKey);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid API Key", response.getBody().get("error"));
    }
    @Test
    public void getHistoricalWeather_ValidApiKey_ReturnsWeatherData() {
        // Arrange
        String city = "ValidCity";
        String date = "01-01-2021";
        String apiKey = "ValidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(true);
        when(weatherService.getHistoricalWeather(city, date)).thenReturn(ResponseEntity.ok(Map.of("data", "WeatherData")));

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getHistoricalWeather(city, date, apiKey);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("WeatherData", response.getBody().get("data"));
    }

    @Test
    public void getHistoricalWeather_InvalidApiKey_ReturnsError() {
        // Arrange
        String city = "ValidCity";
        String date = "01-01-2021";
        String apiKey = "InvalidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getHistoricalWeather(city, date, apiKey);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid API Key", response.getBody().get("error"));
    }

    @Test
    public void getForecastWeather_ValidApiKey_ReturnsWeatherData() {
        // Arrange
        String city = "ValidCity";
        String apiKey = "ValidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(true);
        when(weatherService.getForecastWeather(city)).thenReturn(ResponseEntity.ok(Map.of("data", "WeatherData")));

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getForecastWeather(city, apiKey);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("WeatherData", response.getBody().get("data"));
    }

    @Test
    public void getForecastWeather_InvalidApiKey_ReturnsError() {
        // Arrange
        String city = "ValidCity";
        String apiKey = "InvalidApiKey";
        when(userService.validateApiKey(apiKey)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherImpl.getForecastWeather(city, apiKey);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid API Key", response.getBody().get("error"));
    }

    // Similar tests can be written for getHistoricalWeather and getForecastWeather methods
}