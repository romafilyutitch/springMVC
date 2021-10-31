package com.epam.esm;

import org.springframework.hateoas.RepresentationModel;

/**
 * Custom error class to display
 * when exception in controller occurs
 */
public class Error extends RepresentationModel<Error> {
    private ErrorCode errorCode;
    private final String message;

    public Error(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
