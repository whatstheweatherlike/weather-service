package io.whatstheweatherlike.weather_service.mvc;

import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import io.whatstheweatherlike.weather_service.service.WeatherService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DefaultController defaultController;

    @MockBean
    private WeatherService weatherService;

    @Mock
    private CoordinatedWeatherData coordinatedWeatherData;

    @Before
    public void setUp() {
        when(weatherService.request(eq(0.1), eq(-1.2))).thenReturn(coordinatedWeatherData);
    }

    @Test
    public void returnsWeather() {
        assertThat(defaultController.weather(0.1, -1.2), is(sameInstance(coordinatedWeatherData)));
    }

    @Test
    public void exposesRestInterface() {
        ResponseEntity<CoordinatedWeatherData> coordinatedWeatherDataResponseEntity =
                testRestTemplate.getForEntity("/weather-at/0.1,-1.2", CoordinatedWeatherData.class);
        CoordinatedWeatherData body = coordinatedWeatherDataResponseEntity.getBody();
        assertThat(body, is(notNullValue()));
        assertThat(coordinatedWeatherDataResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

}