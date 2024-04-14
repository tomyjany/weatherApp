package com.inn.weatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SpringBootApplication
public class WeatherAppApplication {
	private static final Logger logger = LoggerFactory.getLogger(WeatherAppApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(WeatherAppApplication.class, args);
	}

}
/*
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WeatherAppApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WeatherAppApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WeatherAppApplication.class, args);
	}
}

 */

