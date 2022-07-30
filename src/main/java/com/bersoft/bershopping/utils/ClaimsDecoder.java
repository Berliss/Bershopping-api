package com.bersoft.bershopping.utils;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class ClaimsDecoder {

    private ClaimsDecoder() {
    }

    public static String getEmailFromClaims(JwtAuthenticationToken principal) {
        return principal.getToken().getClaims().get("email").toString();
    }
}
