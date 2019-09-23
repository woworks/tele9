package com.woworks.client9.restclient;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Base64;

public interface BasicService {

    @ConfigProperty(name = "md.999.auth.token")
    default String generateAuthHeader(String token) {
        return "Basic " + new String(Base64.getEncoder().encode((token + ":").getBytes()));
    }
}
