package io.whatstheweatherlike.weather_service.service;

import io.whatstheweatherlike.weather_service.dto.CoordinatedWeatherData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(WeatherService.class)
public class WeatherServiceTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private WeatherService weatherService;

    private byte[] defaultResponse;

    @Before
    public void readDefaultResponse() throws IOException {
        defaultResponse = FileCopyUtils.copyToByteArray(WeatherService.class.getResourceAsStream("/lat_lon_weather_data.json"));
    }

    @Test
    public void checkRequest() {
        doRequest();

        server.verify();
    }

    @Test
    public void responseIsWeatherData() {
        assertNotNull(doRequest());
    }


    private CoordinatedWeatherData doRequest() {
        server.expect(requestTo(startsWith("/data/2.5/weather")))
                .andExpect(queryParam("lat", is("0.1")))
                .andExpect(queryParam("lon", is("1.2")))
                .andExpect(queryParam("APPID", is("42")))
                .andExpect(method(GET))
                .andRespond(withSuccess(defaultResponse, MediaType.APPLICATION_JSON_UTF8));

        return weatherService.request(0.1, 1.2);
    }

}