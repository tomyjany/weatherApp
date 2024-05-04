package com.inn.weatherApp.service;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = "api/frontend")
public interface Frontend {
    @RequestMapping(path = "/getToken")
    public String getFrontendToken();


}
