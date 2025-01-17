package com.inn.weatherApp.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = "api/subscriber-only")
public interface Subscriber {
    @PostMapping(path = "/test")
    public ResponseEntity<String> youAreSubscribed();
}
