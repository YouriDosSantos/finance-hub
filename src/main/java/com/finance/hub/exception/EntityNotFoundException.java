package com.finance.hub.exception;


public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message){
        super(message);
    }
}
