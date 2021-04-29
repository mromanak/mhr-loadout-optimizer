package com.mromanak.loadoutoptimizer.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRepositoryApiRequestException extends LoadoutOptimizerException {
    public BadRepositoryApiRequestException() {
        super();
    }

    public BadRepositoryApiRequestException(String message) {
        super(message);
    }

    public BadRepositoryApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRepositoryApiRequestException(Throwable cause) {
        super(cause);
    }
}
