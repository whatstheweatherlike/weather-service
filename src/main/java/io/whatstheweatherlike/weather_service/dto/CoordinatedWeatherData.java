package io.whatstheweatherlike.weather_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"base", "visibility", "sys"})
public class CoordinatedWeatherData {

    @JsonProperty(value = "coord")
    private Coordinates coordinates;

    @JsonProperty(value = "weather")
    private List<WeatherCategory> weatherCategories;

    @JsonProperty(value = "main")
    private Temperature temperature;

    @JsonProperty(value = "id")
    private int cityId;

    @JsonProperty(value = "name")
    private String cityName;

    @JsonProperty(value = "wind")
    private Wind wind;

    @JsonProperty(value="clouds")
    private Clouds clouds;

    @JsonProperty(value="rain")
    private Rain rain;

    @JsonProperty(value="snow")
    private Snow snow;

    @JsonProperty(value="dt")
    private int unixDateTime;

    @JsonProperty(value="cod")
    private int httpStatusCode;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<WeatherCategory> getWeatherCategories() {
        return weatherCategories;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Rain getRain() {
        return rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public static final class Coordinates {

        @JsonProperty(value = "lat")
        private double latitude;

        @JsonProperty(value = "lon")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public static final class WeatherCategory {

        @JsonProperty(value = "id")
        private int id;

        @JsonProperty(value = "main")
        private String group;

        @JsonProperty(value = "description")
        private String description;

        @JsonProperty(value = "icon")
        private String iconId;

    }

    public static final class Temperature {

        @JsonProperty(value = "temp")
        private double temperature;

        @JsonProperty(value = "pressure")
        private double pressure;

        @JsonProperty(value = "humidity")
        private double humidity;

        @JsonProperty(value = "temp_min")
        private double minimumTemperature;

        @JsonProperty(value = "temp_max")
        private double maximumTemperature;

        @JsonProperty(value = "sea_level")
        private double seaLevelPressure;

        @JsonProperty(value = "grnd_level")
        private double groundLevelPressure;


    }

    public static final class Wind {

        @JsonProperty(value = "speed")
        private double speed;

        @JsonProperty(value = "deg")
        private int degrees;

    }

    public static final class Clouds {

        @JsonProperty(value="all")
        private int cloudinessPercentage;

    }

    public static final class Rain {

        @JsonProperty(value="3h")
        private int volume;

    }

    public static final class Snow {

        @JsonProperty(value="3h")
        private int volume;

    }
}
