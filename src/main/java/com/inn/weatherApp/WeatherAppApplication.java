package com.inn.weatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;1

@SpringBootApplication
public class WeatherAppApplication {
	private static final Logger logger = LoggerFactory.getLogger(WeatherAppApplication.class);


	public static void main(String[] args) {
		logger.info("Database URL: {}", System.getenv("SPRING_DATASOURCE_URL"));
		SpringApplication.run(WeatherAppApplication.class, args);
	}

}
