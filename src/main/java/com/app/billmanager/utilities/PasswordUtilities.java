package com.app.billmanager.utilities;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class PasswordUtilities {

    public static String generatePassword(){
        return new SecureRandom()
                .ints(25, '!', '{')
                .mapToObj(i -> String.valueOf((char)i))
                .collect(Collectors.joining());
    }

}
