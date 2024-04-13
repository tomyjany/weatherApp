package com.inn.weatherApp.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
public class WeatherUtility {
    private WeatherUtility(){};

    public static ResponseEntity<String> getResponse(String responseMessage,HttpStatus httpstatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}",httpstatus);
    }

}
