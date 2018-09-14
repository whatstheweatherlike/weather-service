package io.whatstheweatherlike.weather_service.mvc;

import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import io.whatstheweatherlike.weather_service.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DefaultController {

    private final WeatherService weatherService;

    @Autowired
    public DefaultController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(path="/weather-at/{latitude},{longitude}")
    @ResponseBody
    public CoordinatedWeatherData weather(@PathVariable(name = "latitude") double latitude, @PathVariable(name="longitude") double longitude) {
        return weatherService.request(latitude, longitude);
    }

}
