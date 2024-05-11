package com.inn.weatherApp.serviceImpl;

import Objects.CityInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.weatherApp.service.WeatherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherServiceImpl weatherService;



    private static String currentWeatherJson;
    private static String forecastWeatherJson;
    private static String historicalWeatherJson;
    private static String geoLocationJson;

    @BeforeAll
    public static void setup() {
        currentWeatherJson = """
        {"coord":{"lon":15.0562,"lat":50.7671},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"base":"stations","main":{"temp":290.48,"feels_like":290.04,"temp_min":287.92,"temp_max":290.94,"pressure":1010,"humidity":68,"sea_level":1010,"grnd_level":955},"visibility":10000,"wind":{"speed":3.67,"deg":211,"gust":7.9},"clouds":{"all":100},"dt":1714925586,"sys":{"type":2,"id":2008798,"country":"CZ","sunrise":1714879572,"sunset":1714933597},"timezone":7200,"id":3071961,"name":"Liberec","cod":200}
        """;

        forecastWeatherJson = """
        {"cod":"200","message":0,"cnt":1,"list":[{"dt":1714928400,"main":{"temp":289.8,"feels_like":289.4,"temp_min":287.59,"temp_max":289.8,"pressure":1010,"sea_level":1010,"grnd_level":966,"humidity":72,"temp_kf":2.21},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":3.43,"deg":224,"gust":8.17},"visibility":10000,"pop":1,"rain":{"1h":0.64},"sys":{"pod":"d"},"dt_txt":"2024-05-05 17:00:00"}],"city":{"id":3071961,"name":"Liberec","coord":{"lat":50.7703,"lon":15.0584},"country":"CZ","population":97770,"timezone":7200,"sunrise":1714879571,"sunset":1714933597}}
        """;

        historicalWeatherJson = """
        {"message":"Count: 1","cod":"200","city_id":1,"calctime":0.072752276,"cnt":1,"list":[{"dt":1712167200,"main":{"temp":299.89,"feels_like":301.58,"pressure":1013,"humidity":70,"temp_min":299.89,"temp_max":299.89},"wind":{"speed":2.5,"deg":89,"gust":2.64},"clouds":{"all":95},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}]}]}
        
        """;
        geoLocationJson = """
                [
                    {
                        "name": "ValidCity",
                        "local_names": {
                            "de": "Münchengrätz",
                            "da": "Mnichovo Hradiste",
                            "ru": "Мнихово-Градиште",
                            "cs": "Mnichovo Hradiště",
                            "la": "Gredis Monachorum",
                            "lt": "Mnichovo Hradištė"
                        },
                        "lat": 50.7671,
                        "lon": 15.0562,
                        "country": "CZ",
                        "state": "Central Bohemia"
                    }
                ]
                """;
    }

    @Test
    public void getCurrentWeather_ValidCity_ReturnsWeatherData() {
        // Arrange
        String city = "ValidCity";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(currentWeatherJson);

        // Act
        var response = weatherService.getCurrentWeather(city);

        // Assert
        assertNotNull(response);
        // Add more assertions based on your expected response
    }


    @Test
    public void getCurrentWeather_InvalidCity_ReturnsError() {
        // Arrange
        String city = "InvalidCity";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{\"cod\":\"404\",\"message\":\"city not found\"}");

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getCurrentWeather(city);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error getting coordinates for the city", response.getBody().get("error"));
    }



    @Test
    public void getHistoricalWeather_InvalidCity_ReturnsError() {
        // Arrange
        String city = "InvalidCity";
        String date = "01-01-2021";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{\"cod\":\"404\",\"message\":\"city not found\"}");

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getHistoricalWeather(city, date);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error getting coordinates for the city", response.getBody().get("error"));
    }



    @Test
    public void getForecastWeather_InvalidCity_ReturnsError() {
        // Arrange
        String city = "InvalidCity";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{\"cod\":\"404\",\"message\":\"city not found\"}");

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getForecastWeather(city);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error getting coordinates for the city", response.getBody().get("error"));
    }

    @Test
    public void getCurrentWeather_ValidResponse_ParsedCorrectly() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";
        double lon = 15.0562;
        double lat = 50.7671;

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return the currentWeatherJson for getCurrentWeather
        when(restTemplate.getForObject(contains("data/2.5/weather"), eq(String.class))).thenReturn(currentWeatherJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getCurrentWeather(city);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("overcast clouds", responseBody.get("description"));
        assertEquals("04d", responseBody.get("icon"));
        assertEquals(city, responseBody.get("city"));
        assertEquals(lon, responseBody.get("lon"));
        assertEquals(lat, responseBody.get("lat"));
    }
    @Test
    public void getCurrentWeather_InvalidJsonResponse_ReturnsInternalServerError() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return an invalid JSON string for getCurrentWeather
        String invalidJson = "invalid json";
        when(restTemplate.getForObject(contains("data/2.5/weather"), eq(String.class))).thenReturn(invalidJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getCurrentWeather(city);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error parsing the API response", response.getBody().get("error"));
    }

    @Test
    public void getHistoricalWeather_ValidCityAndDate_ReturnsWeatherData() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";
        String date = "01-01-2021";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return the historicalWeatherJson for getHistoricalWeather
        when(restTemplate.getForObject(contains("data/2.5/history/city"), eq(String.class))).thenReturn(historicalWeatherJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getHistoricalWeather(city, date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Add more assertions based on your expected response
    }


    @Test
    public void getHistoricalWeather_InvalidDateFormat_ReturnsError() {
        // Arrange
        String city = "ValidCity";
        String date = "InvalidDate";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getHistoricalWeather(city, date);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format. Please use 'dd-MM-yyyy'", response.getBody().get("error"));
    }

    @Test
    public void getHistoricalWeather_ErrorMakingApiCall_ReturnsError() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";
        String date = "01-01-2021";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to throw a RestClientException for getHistoricalWeather
        when(restTemplate.getForObject(contains("data/2.5/history/city"), eq(String.class))).thenThrow(new RestClientException("Error making the API call"));

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getHistoricalWeather(city, date);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").toString().contains("Error making the API call"));
    }

    @Test
    public void getHistoricalWeather_ErrorParsingApiResponse_ReturnsError() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";
        String date = "01-01-2021";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return an invalid JSON string for getHistoricalWeather
        String invalidJson = "invalid json";
        when(restTemplate.getForObject(contains("data/2.5/history/city"), eq(String.class))).thenReturn(invalidJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getHistoricalWeather(city, date);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error parsing the API response", response.getBody().get("error"));
    }
    @Test
    public void getForecastWeather_ValidCity_ReturnsWeatherData() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return the forecastWeatherJson for getForecastWeather
        when(restTemplate.getForObject(contains("data/2.5/forecast/hourly"), eq(String.class))).thenReturn(forecastWeatherJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getForecastWeather(city);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Add more assertions based on your expected response
    }

    @Test
    public void getForecastWeather_ErrorMakingApiCall_ReturnsError() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to throw a RestClientException for getForecastWeather
        when(restTemplate.getForObject(contains("data/2.5/forecast/hourly"), eq(String.class))).thenThrow(new RestClientException("Error making the API call"));

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getForecastWeather(city);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").toString().contains("Error making the API call"));
    }

    @Test
    public void getForecastWeather_ErrorParsingApiResponse_ReturnsError() throws JsonProcessingException {
        // Arrange
        String city = "ValidCity";

        // Mock the restTemplate.getForObject method to return the geoLocationJson for getLonLat
        when(restTemplate.getForObject(contains("geo/1.0/direct"), eq(String.class))).thenReturn(geoLocationJson);

        // Mock the restTemplate.getForObject method to return an invalid JSON string for getForecastWeather
        String invalidJson = "invalid json";
        when(restTemplate.getForObject(contains("data/2.5/forecast/hourly"), eq(String.class))).thenReturn(invalidJson);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherService.getForecastWeather(city);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error parsing the API response", response.getBody().get("error"));
    }









}