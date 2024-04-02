package com.inn.weatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class WeatherAppApplication {


	public static void main(String[] args) {
		SpringApplication.run(WeatherAppApplication.class, args);
	}

}
