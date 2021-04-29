package com.mromanak.loadoutoptimizer.model.exception;

public class LoadoutOptimizerException extends RuntimeException {
    public LoadoutOptimizerException() {
        super();
    }

    public LoadoutOptimizerException(String message) {
        super(message);
    }

    public LoadoutOptimizerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadoutOptimizerException(Throwable cause) {
        super(cause);
    }
}
