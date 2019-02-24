package io.whatstheweatherlike.weather_service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class WeatherServiceApplication {

    public static final double[] DEFAULT_PERCENTILES = {0.5, 0.85, 0.95, 0.99};

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
                e.printStackTrace();
                throw new RuntimeException("Could not configure metrics registry! " + e.getLocalizedMessage());
            }
        };
    }

}
