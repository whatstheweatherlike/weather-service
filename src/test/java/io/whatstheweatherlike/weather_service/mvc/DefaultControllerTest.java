package io.whatstheweatherlike.weather_service.mvc;

import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import io.whatstheweatherlike.weather_service.service.WeatherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    public void returnsWeather() {
        when(weatherService.request(eq(0.1), eq(-1.2))).thenReturn(coordinatedWeatherData);
        DeferredResult<CoordinatedWeatherData> weather = defaultController.weather(0.1, -1.2);
        await().atMost(1, TimeUnit.SECONDS).until(() -> weather.isSetOrExpired());
        assertThat(weather.getResult(), is(sameInstance(coordinatedWeatherData)));
    }

    @Test
    public void throwsTimeoutException() {
        Answer<CoordinatedWeatherData> coordinatedWeatherDataAnswer = invocation -> {
            final Object mutex = new Object();
            long start = System.currentTimeMillis();
            System.out.println("Start wait");
            synchronized (mutex) {
                mutex.wait(3000L);
            }
            System.out.println(String.format("Stop wait, %s msecs elapsed", System.currentTimeMillis() - start));
            return coordinatedWeatherData;
        };
        when(weatherService.request(eq(0.1), eq(-1.2))).thenAnswer(coordinatedWeatherDataAnswer);
        ResponseEntity<CoordinatedWeatherData> coordinatedWeatherDataResponseEntity =
                testRestTemplate.getForEntity("/weather-at/0.1,-1.2", CoordinatedWeatherData.class);
        assertThat(coordinatedWeatherDataResponseEntity.getStatusCode(), is(HttpStatus.REQUEST_TIMEOUT));
        assertThat(coordinatedWeatherDataResponseEntity.getBody().getHttpStatusCode(), is(HttpStatus.REQUEST_TIMEOUT.value()));
    }

    @Test
    public void exposesRestInterface() {
        when(weatherService.request(eq(0.1), eq(-1.2))).thenReturn(coordinatedWeatherData);
        ResponseEntity<CoordinatedWeatherData> coordinatedWeatherDataResponseEntity =
                testRestTemplate.getForEntity("/weather-at/0.1,-1.2", CoordinatedWeatherData.class);
        CoordinatedWeatherData body = coordinatedWeatherDataResponseEntity.getBody();
        assertThat(body, is(notNullValue()));
        assertThat(coordinatedWeatherDataResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

}