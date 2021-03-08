package com.app.billmanager.exceptions;

import org.springframework.security.core.AuthenticationException;

public class NoEmailAuthenticationException extends AuthenticationException {

    public NoEmailAuthenticationException(String msg) {
        super(msg);
    }

}
