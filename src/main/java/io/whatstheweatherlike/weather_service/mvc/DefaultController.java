package io.whatstheweatherlike.weather_service.mvc;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import io.whatstheweatherlike.weather_service.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@Timed
public class DefaultController {

    private final WeatherService weatherService;
    private final Timer weatherAtPercentiles;

    @Autowired
    public DefaultController(WeatherService weatherService, MeterRegistry registry) {
        this.weatherService = weatherService;
        weatherAtPercentiles = Timer.builder("rest.weather-at")
                .publishPercentiles(0.5, 0.85, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(100))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/weather-at/{latitude},{longitude}")
    @ResponseBody
    public CoordinatedWeatherData weather(@PathVariable(name = "latitude") double latitude, @PathVariable(name = "longitude") double longitude) {
        try {
            return weatherAtPercentiles.recordCallable(() -> weatherService.request(latitude, longitude));
        } catch (Exception e) {
            throw new RestServiceException(e);
        }
    }

}
