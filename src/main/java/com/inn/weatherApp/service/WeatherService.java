package com.inn.weatherApp.service;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WeatherService {
    public ResponseEntity<Map<String, Object>> getCurrentWeather(String city);
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(String city, String date);
    public ResponseEntity<Map<String, Object>> getForecastWeather(String city);
}
