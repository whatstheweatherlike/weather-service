package io.whatstheweatherlike.weather_service.service;

import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    // TODO: inject via property setter
    private String appId = "42";

    @Autowired
    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public CoordinatedWeatherData request(double latitude, double longitude) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_UTF8.toString());

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/data/2.5/weather")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("APPID", appId);

        return restTemplate.getForObject(builder.toUriString(), CoordinatedWeatherData.class);
    }

}
