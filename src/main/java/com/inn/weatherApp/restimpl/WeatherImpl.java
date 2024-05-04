package com.inn.weatherApp.restimpl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.weatherApp.rest.Weather;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class WeatherImpl implements Weather {
    private final WeatherService weatherService;
    private final UserService userService;

    @Value("${frontend.apiKey}")
    private String frontendApiKey;

    public WeatherImpl(WeatherService weatherService, UserService userService) {
        this.weatherService = weatherService;
        this.userService = userService;
    }


    @Override
    public ResponseEntity<Map<String, Object>> getCurrentWeather(String city, String apiKey) {
        if(!apiKey.equals(frontendApiKey) && !userService.validateApiKey(apiKey)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid API Key"));
        }
        return weatherService.getCurrentWeather(city);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(String city, String date, String apiKey) {
        if(!apiKey.equals(frontendApiKey) && !userService.validateApiKey(apiKey)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid API Key"));
        }
        return weatherService.getHistoricalWeather(city, date);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getForecastWeather(String city, String apiKey) {
        if(!apiKey.equals(frontendApiKey) && !userService.validateApiKey(apiKey)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid API Key"));
        }
        return weatherService.getForecastWeather(city);
    }

}
