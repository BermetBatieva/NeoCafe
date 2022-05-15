package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class TableIsOccupiedException extends RuntimeException{

    public TableIsOccupiedException() {
        super("This table is occupied. Chose a free one.");
    }

}
