package com.inn.weatherApp.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping(path = "api/weather")
public interface Weather {
    @GetMapping(path = "/current")
    public ResponseEntity<Map<String, Object>> getCurrentWeather(@RequestParam("c") String city,@RequestParam("k") String apiKey);
    @GetMapping(path = "/historical")
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(@RequestParam("c") String city, @RequestParam("d") String date,@RequestParam("k") String apiKey);
    @GetMapping(path = "/forecast")
    public ResponseEntity<Map<String, Object>> getForecastWeather(@RequestParam("c") String city,@RequestParam("k") String apiKey);

}
