package io.whatstheweatherlike.weather_service.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final Timer backendServicePercentiles;

    @Value("#{systemProperties['APPID']?:'42'}")
    private String appId;

    @Autowired
    public WeatherService(RestTemplate restTemplate, MeterRegistry registry, double[] defaultPercentiles) {
        this.restTemplate = restTemplate;
        backendServicePercentiles = Timer.builder("backendservice.weather")
                .publishPercentiles(defaultPercentiles)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(500))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
    }

    public CoordinatedWeatherData request(double latitude, double longitude) {
        LOGGER.info("Using APPID: {}", appId.substring(0, Math.min(appId.length(), 3)) + "*********");
        try {
            return backendServicePercentiles.recordCallable(() -> restTemplate.getForObject(UriComponentsBuilder.fromPath("/data/2.5/weather")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("units", "metric")
                    .queryParam("APPID", appId).toUriString(), CoordinatedWeatherData.class));
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

}
