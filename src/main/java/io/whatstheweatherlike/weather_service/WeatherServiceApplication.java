package io.whatstheweatherlike.weather_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherServiceApplication {

    // TODO: inject via property setter
    @Value("${weatherService.hostAndPort}")
    private String serviceHostAndPort = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication.run(WeatherServiceApplication.class, args);
    }

}
