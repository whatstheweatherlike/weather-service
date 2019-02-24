package io.whatstheweatherlike.weather_service.mvc;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class RestServiceException extends RuntimeException {

    public RestServiceException(Throwable cause) {
        super(cause);
    }

}
