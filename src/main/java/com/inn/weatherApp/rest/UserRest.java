package com.inn.weatherApp.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.Map;



@RequestMapping(path = "/user")
public interface UserRest {
    @PostMapping(path="/signup")
    public ResponseEntity<String> singUp(@RequestBody() Map<String,String> requestMap);

}
