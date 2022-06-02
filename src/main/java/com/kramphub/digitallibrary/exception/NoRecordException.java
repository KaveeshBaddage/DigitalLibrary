package com.kramphub.digitallibrary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * NoRecordException class use to handle the exception occurs when there is no data to return as response.
 * * @author Kaveesha Baddage
 * *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoRecordException extends RuntimeException {

    public NoRecordException(String ex) {
        super(ex);
    }
}
