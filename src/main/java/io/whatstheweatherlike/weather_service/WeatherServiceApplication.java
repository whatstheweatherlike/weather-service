package io.whatstheweatherlike.weather_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WeatherServiceApplication {

    @Value("${weatherService.hostAndPort}")
    private String serviceHostAndPort;

    public static void main(String[] args) {
        SpringApplication.run(WeatherServiceApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().rootUri(serviceHostAndPort).build();
    }

}
