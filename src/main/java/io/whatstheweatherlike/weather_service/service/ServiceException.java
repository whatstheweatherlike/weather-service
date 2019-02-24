package io.whatstheweatherlike.weather_service.service;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ServiceException extends RuntimeException {

    public ServiceException(Exception e) {
        super(e);
    }

}
