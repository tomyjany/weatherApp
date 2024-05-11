package com.inn.weatherApp.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;



@RequestMapping(path = "api/user")
public interface UserRest {
    @PostMapping(path="/signup")
    public ResponseEntity<String> singUp(@RequestBody() Map<String,String> requestMap);
    @PostMapping(path="/signin")
    public ResponseEntity<Map<String,String>> signIn(@RequestBody() Map<String,String> requestMap);
    @PostMapping(path="/test")
    public ResponseEntity<String> testUser();
    @PutMapping(path="/pay")
    public ResponseEntity<String> pay(@RequestBody String token);
    @PostMapping(path = "/addfavorite")
    public ResponseEntity<String> addFavoriteCity(@RequestParam("c") String city);
    @GetMapping(path = "/favorites")
    public ResponseEntity<List<String>> getFavoriteCities();

}
