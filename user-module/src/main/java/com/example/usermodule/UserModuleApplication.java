package com.example.usermodule;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;
import java.util.Base64;

@SpringBootApplication
public class UserModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserModuleApplication.class, args);
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Generates a secure 512-bit key
        String base64EncodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated Key: " + base64EncodedKey);
    }

}
