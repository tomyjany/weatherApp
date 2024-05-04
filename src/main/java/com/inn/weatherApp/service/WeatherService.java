package com.inn.weatherApp.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WeatherService {
    public ResponseEntity<Map<String, Object>> getCurrentWeather(String city);
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(String city, String date);
}
