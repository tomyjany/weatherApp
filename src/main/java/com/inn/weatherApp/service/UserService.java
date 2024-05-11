package com.inn.weatherApp.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String,String> requestMap);
    ResponseEntity<Map<String,String>> signIn(Map<String,String> requestMap);

    ResponseEntity<String> pay(String token);
    boolean validateApiKey(String apiKey);
    Integer findUserByEmailAddress(String email);
    ResponseEntity<String> addFavoriteCity(String email,String city);

    ResponseEntity<List<String>> getFavoriteCities(String email);
}
