package com.banking.clienteservice.application.exception;

public class ClienteAlreadyExistsException extends RuntimeException {
    
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}

