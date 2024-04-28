package com.inn.weatherApp.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;



@RequestMapping(path = "api/user")
public interface UserRest {
    @PostMapping(path="/signup")
    public ResponseEntity<String> singUp(@RequestBody() Map<String,String> requestMap);
    @PostMapping(path="/signin")
    public ResponseEntity<String> signIn(@RequestBody() Map<String,String> requestMap);
    @PostMapping(path="/test")
    public ResponseEntity<String> testUser();
    @PutMapping(path="/pay")
    public ResponseEntity<String> pay(@RequestBody String token);

}
