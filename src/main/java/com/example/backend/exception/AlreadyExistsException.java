package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyExistsException extends Exception{

    public AlreadyExistsException(){
    }

    public AlreadyExistsException(String massage){
        super(massage);
    }

}
