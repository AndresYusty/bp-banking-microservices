package com.banking.cuentaservice.application.exception;

public class CuentaNotFoundException extends RuntimeException {
    
    public CuentaNotFoundException(String message) {
        super(message);
    }
}

