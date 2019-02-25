package io.whatstheweatherlike.weather_service;

import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class WeatherServiceApplication {

    private static final double[] DEFAULT_PERCENTILES = {0.5, 0.85, 0.95, 0.99};

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

    @Bean
    public MeterRegistryCustomizer meterRegistryCustomizer() {
        return registry -> {
            try {
                registry.config().commonTags(
                        Arrays.asList(
                                Tag.of("stage", "dev"),
                                Tag.of("ip", InetAddress.getLocalHost().getHostAddress())
                        )
                );
            } catch (UnknownHostException e) {
                throw new ApplicationException("Could not retrieve local IP address!", e);
            }
        };
    }

    @Bean
    public double[] defaultPercentiles() {
        return DEFAULT_PERCENTILES;
    }

    private static final class ApplicationException extends RuntimeException {
        private ApplicationException(String message, Throwable e) {
            super(message, e);
        }
    }

}
