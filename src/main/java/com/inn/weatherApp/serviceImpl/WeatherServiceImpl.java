package com.inn.weatherApp.serviceImpl;

import Objects.CityInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.weatherApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.ZoneId;
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
        CityInfo cityInfo;
        try {
            cityInfo = getLonLat(city);
            log.info("coordinates for current weather: {}, {}", cityInfo.getLatitude(), cityInfo.getLongitude());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error getting coordinates for the city"));
        }

        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                cityInfo.getLatitude(), cityInfo.getLongitude(), apiKey);
        log.info("coordinates for current weather: {}, {}", cityInfo.getLatitude(), cityInfo.getLongitude());

        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error making the API call"));
        }

        try {
            Map<String, Object> parsedResponse = parseCurrentWeatherResponse(response,cityInfo.getName(), cityInfo.getLatitude(), cityInfo.getLongitude());
            return ResponseEntity.ok(parsedResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error parsing the API response"));
        }
    }


    @Override
    public ResponseEntity<Map<String, Object>> getHistoricalWeather(String city, String date) {
        CityInfo cityInfo;
        try {
            cityInfo = getLonLat(city);
            log.info("Coordinates: {}, {}", cityInfo.getLatitude(), cityInfo.getLongitude());
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
                localDate.atTime(6, 59).atZone(ZoneId.of("CET")),
                localDate.atTime(11, 59).atZone(ZoneId.of("CET")),
                localDate.atTime(17, 59).atZone(ZoneId.of("CET")),
                localDate.atTime(22, 59).atZone(ZoneId.of("CET"))
        };

        long[] timestamps = new long[times.length];
        for (int i = 0; i < times.length; i++) {
            timestamps[i] = times[i].toEpochSecond();
        }

        List<Map<String, Object>> weatherDataList = new ArrayList<>();
        for (long timestamp : timestamps) {
            try {
                String url = String.format("https://history.openweathermap.org/data/2.5/history/city?lat=%s&lon=%s&type=hour&start=%s&cnt=1&appid=%s",
                        cityInfo.getLatitude(),cityInfo.getLongitude(), timestamp, apiKey);
                log.info("Requesting data for Unix timestamp: {}", timestamp);
                String response = restTemplate.getForObject(url, String.class);
                Map<String, Object> parsedResponse = parseHistoricalWeatherResponse(response);
                if (parsedResponse.containsKey("error")) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(parsedResponse);
                }
                //parsedResponse.put("time", timestamp);
                parsedResponse.put("city", cityInfo.getName());
                parsedResponse.put("lon", cityInfo.getLatitude());
                parsedResponse.put("lat", cityInfo.getLongitude());
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

    @Override
    public ResponseEntity<Map<String, Object>> getForecastWeather(String city) {
        CityInfo cityInfo;
        try {
            cityInfo = getLonLat(city);
            log.info("coordinates for forecast weather: {}, {}", cityInfo.getLatitude(), cityInfo.getLongitude());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error getting coordinates for the city"));
        }

        String url = String.format("https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=%s&lon=%s&cnt=1&appid=%s",
                cityInfo.getLatitude(),cityInfo.getLongitude(), apiKey);

        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error making the API call"));
        }

        try {
            Map<String, Object> parsedResponse = parseForecastWeatherResponse(response,cityInfo.getName() , cityInfo.getLatitude(),cityInfo.getLatitude());
            return ResponseEntity.ok(parsedResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error parsing the API response"));
        }
    }
    private Map<String, Object> parseForecastWeatherResponse(String response, String city, double lon, double lat) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        Map<String, Object> weatherData = new HashMap<>();

        // Extract the weather description and icon
        JsonNode listNode = root.get("list");
        if (listNode != null && listNode.isArray() && listNode.size() > 0) {
            JsonNode weatherNode = listNode.get(0).get("weather");
            if (weatherNode != null && weatherNode.isArray() && weatherNode.size() > 0) {
                String weatherDescription = weatherNode.get(0).get("description").asText();
                String icon = weatherNode.get(0).get("icon").asText();
                weatherData.put("description", weatherDescription);
                weatherData.put("icon", icon);
            }

            // Extract the time
            long time = listNode.get(0).get("dt").asLong();
            weatherData.put("time", time);
        }

        // Add city, lon, lat to the response
        weatherData.put("city", city);
        weatherData.put("lon", lon);
        weatherData.put("lat", lat);

        if (weatherData.isEmpty()) {
            weatherData.put("error", "No weather data available");
        }

        return weatherData;
    }

    public CityInfo getLonLat(String city) throws JsonProcessingException {
        String url = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", city, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        if (root.size() > 0) {
            JsonNode cityNode = root.get(0);
            if (cityNode != null) {
                String correctedCityName = cityNode.get("name").asText();
                double latitude = cityNode.get("lat").asDouble();
                double longitude = cityNode.get("lon").asDouble();

                return new CityInfo(correctedCityName, longitude, latitude);
            }
        }

        throw new JsonMappingException(null, "No city data available");
    }
    private Map<String, Object> parseHistoricalWeatherResponse(String response) throws JsonProcessingException {
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
    private Map<String, Object> parseCurrentWeatherResponse(String response,String city, double lat, double lon) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        Map<String, Object> weatherData = new HashMap<>();

        // Extract the weather description and icon
        JsonNode weatherNode = root.get("weather");
        if (weatherNode != null && weatherNode.isArray() && weatherNode.size() > 0) {
            String weatherDescription = weatherNode.get(0).get("description").asText();
            String icon = weatherNode.get(0).get("icon").asText();
            weatherData.put("description", weatherDescription);
            weatherData.put("icon", icon);
            weatherData.put("city",city);
        }

        // Extract the time
        long time = root.get("dt").asLong();
        weatherData.put("time", time);

        // Add city, lon, lat to the response
        weatherData.put("lon", lon);
        weatherData.put("lat", lat);

        if (weatherData.isEmpty()) {
            weatherData.put("error", "No weather data available");
        }

        return weatherData;
    }
}
