whatstheweatherlike weather_service
===

[![Build Status](https://travis-ci.com/whatstheweatherlike/weather-service.svg?branch=master)](https://travis-ci.com/whatstheweatherlike/weather-service)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=whatstheweatherlike_weather-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=whatstheweatherlike_weather-service)

Backend REST service responsible for retrieving weather data for the provided location.

Requirements:
* [gradle](https://gradle.org/)
* [docker](https://www.docker.com/)
* an appid required to use [The Openweathermap API](https://openweathermap.org/api)

In a nutshell
---

**`build_and_test.sh` - Run weather_service as docker container**

Use `build_and_test.sh` to build the project, create a docker image, run a docker container locally and execute a GET request against the REST service.

The script will exit with a non zero code if the service responds with anything else than HTTP status 200 and a json containing the key value pair 'name' -> "Earth".


**`test_in_minikube.sh` - Run weather_service in minikube**

Use `test_in_minikube.sh` to create a minikube deployment and expose the service, then execute a GET request against the REST service.

The script will exit with a non zero code if the service responds with anything else than HTTP status 200.
