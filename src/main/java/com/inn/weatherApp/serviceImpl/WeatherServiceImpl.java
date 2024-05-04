package com.inn.weatherApp.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.weatherApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {


    @Value("${openweather.apiKey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCurrentWeather(String city) {
        double[] coordinates;
        try {
            coordinates = getLonLat(city);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error getting coordinates for the city"));
        }

        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                coordinates[1], coordinates[0], apiKey);

        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error making the API call"));
        }

        try {
            return ResponseEntity.ok(parseResponse(response));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error parsing the API response"));
        }
    }


    @Override
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(String city, String date) {
        double[] coordinates;
        try {
            coordinates = getLonLat(city);
            log.info("Coordinates: {}, {}", coordinates[0], coordinates[1]);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error getting coordinates for the city"));
        }

        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            localDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid date format. Please use 'dd-MM-yyyy'"));
        }

        ZonedDateTime[] times = new ZonedDateTime[] {
                localDate.atTime(6, 59).atZone(ZoneOffset.UTC),
                localDate.atTime(11, 59).atZone(ZoneOffset.UTC),
                localDate.atTime(17, 59).atZone(ZoneOffset.UTC),
                localDate.atTime(22, 59).atZone(ZoneOffset.UTC)
        };

        long[] timestamps = new long[times.length];
        for (int i = 0; i < times.length; i++) {
            timestamps[i] = times[i].toEpochSecond();
        }

        List<Map<String, Object>> weatherDataList = new ArrayList<>();
        for (long timestamp : timestamps) {
            try {
                String url = String.format("https://history.openweathermap.org/data/2.5/history/city?lat=%s&lon=%s&type=hour&start=%s&cnt=1&appid=%s",
                        coordinates[1], coordinates[0], timestamp, apiKey);
                log.info("Requesting data for Unix timestamp: {}", timestamp);
                String response = restTemplate.getForObject(url, String.class);
                Map<String, Object> parsedResponse = parseResponse(response);
                if (parsedResponse.containsKey("error")) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(parsedResponse);
                }
                parsedResponse.put("time", timestamp);
                parsedResponse.put("city", city);
                parsedResponse.put("lon", coordinates[0]);
                parsedResponse.put("lat", coordinates[1]);
                weatherDataList.add(parsedResponse);
            } catch (RestClientException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error making the API call" + e.getMessage()));
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error parsing the API response"));
            }
        }

        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", weatherDataList);

        return ResponseEntity.ok(finalResponse);
    }
    public double[] getLonLat(String city) throws JsonProcessingException {
        String url = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", city, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        double latitude = root.get(0).get("lat").asDouble();
        double longitude = root.get(0).get("lon").asDouble();

        return new double[]{longitude, latitude};
    }
    private Map<String, Object> parseResponse(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        Map<String, Object> weatherData = new HashMap<>();

        // Check if the "list" array exists and is not empty
        JsonNode listNode = root.get("list");
        if (listNode != null && listNode.isArray() && listNode.size() > 0) {
            // Check if the "weather" array exists and is not empty
            JsonNode weatherNode = listNode.get(0).get("weather");
            if (weatherNode != null && weatherNode.isArray() && weatherNode.size() > 0) {
                // Extract the weather description, time, and icon
                String weatherDescription = weatherNode.get(0).get("description").asText();
                String icon = weatherNode.get(0).get("icon").asText();
                long time = listNode.get(0).get("dt").asLong();

                weatherData.put("description", weatherDescription);
                weatherData.put("icon", icon);
                weatherData.put("time", time);
            }
        }

        if (weatherData.isEmpty()) {
            weatherData.put("error", "No weather data available");
        }

        return weatherData;
    }
}
