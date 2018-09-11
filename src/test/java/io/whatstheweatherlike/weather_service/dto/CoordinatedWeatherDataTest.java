package io.whatstheweatherlike.weather_service.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class CoordinatedWeatherDataTest {

    @Test
    public void testDeserializationWorks() {
        assertThat(deserializeWeatherData(), is(notNullValue()));
    }

    @Test
    public void hasCoordinates() {
        assertThat(deserializeWeatherData().getCoordinates(), is(notNullValue()));
    }

    @Test
    public void hasWeather() {
        assertThat(deserializeWeatherData().getWeatherCategories(), allOf(is(notNullValue()), hasItem(notNullValue())));
    }

    @Test
    public void hasTemperature() {
        assertThat(deserializeWeatherData().getTemperature(), is(notNullValue()));
    }

    @Test
    public void hasWind() {
        assertThat(deserializeWeatherData().getWind(), is(notNullValue()));
    }

    @Test
    public void hasClouds() {
        assertThat(deserializeWeatherData().getClouds(), is(notNullValue()));
    }

    @Test
    public void hasRain() {
        assertThat(deserializeWeatherData().getRain(), is(notNullValue()));
    }

    @Test
    public void hasSnow() {
        assertThat(deserializeWeatherData().getSnow(), is(notNullValue()));
    }

    private CoordinatedWeatherData deserializeWeatherData() {
        CoordinatedWeatherData coordinatedWeatherData = null;
        try {
            coordinatedWeatherData = new ObjectMapper().readValue(getClass().getResourceAsStream("/lat_lon_weather_data.json"), CoordinatedWeatherData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return coordinatedWeatherData;
    }

}