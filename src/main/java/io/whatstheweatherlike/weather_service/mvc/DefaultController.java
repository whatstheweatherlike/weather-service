package io.whatstheweatherlike.weather_service.mvc;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import io.whatstheweatherlike.weather_service.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@RestController
@Timed(histogram = true, percentiles = {0.5, 0.85, 0.95, 0.99})
public class DefaultController {

    private final WeatherService weatherService;
    private final Timer weatherAtPercentiles;
    private final Timer responseWritePercentiles;
    private static final CoordinatedWeatherData TIMEOUT_RESPONSE =
            new CoordinatedWeatherData(HttpStatus.REQUEST_TIMEOUT.value());

    @Autowired
    public DefaultController(WeatherService weatherService, MeterRegistry registry, double[] defaultPercentiles) {
        this.weatherService = weatherService;
        weatherAtPercentiles = Timer.builder("service.weather-at")
                .publishPercentiles(defaultPercentiles)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(500))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
        responseWritePercentiles = Timer.builder("rest.response-write")
                .publishPercentiles(defaultPercentiles)
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(500))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/weather-at/{latitude},{longitude}")
    public DeferredResult<CoordinatedWeatherData> weather(@PathVariable(name = "latitude") double latitude,
                                                          @PathVariable(name = "longitude") double longitude) {
        final DeferredResult<CoordinatedWeatherData> output = new DeferredResult<>();
        output.onTimeout(() -> output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(TIMEOUT_RESPONSE)));

        CompletableFuture.runAsync(() -> {
            try {
                CoordinatedWeatherData result = weatherAtPercentiles.recordCallable(() -> weatherService
                        .request(latitude, longitude));
                responseWritePercentiles.record(() -> output.setResult(result));
            } catch (Exception e) {
                output.setErrorResult(new RestServiceException(e));
            }
        });
        return output;
    }

}
