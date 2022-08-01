package com.bersoft.bershopping.utils;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public class ClaimsDecoder {

    private ClaimsDecoder() {
    }

    public static String getEmailFromClaims(JwtAuthenticationToken principal) {
      return   Optional.ofNullable(principal)
                .map(token -> token.getToken().getClaims().get("email").toString())
                .orElse("");
    }
}
