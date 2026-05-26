package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class GoogleOidcService {

    private static final String ISSUER = "https://accounts.google.com";

    @Value("${google.oauth.client-id:}")
    private String clientId;

    private JwtDecoder jwtDecoder;

    public GoogleIdentity verify(String idToken) {
        try {
            Jwt jwt = getJwtDecoder().decode(idToken);
            Boolean emailVerified = jwt.getClaim("email_verified");
            String email = jwt.getClaimAsString("email");

            if (!Boolean.TRUE.equals(emailVerified) || !StringUtils.hasText(email)) {
                log.warn("Google ID token rejected because email is missing or not verified. email={}, emailVerified={}", email, emailVerified);
                throw new AppException(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
            }

            return new GoogleIdentity(
                    jwt.getSubject(),
                    email,
                    jwt.getClaimAsString("name")
            );
        } catch (JwtException exception) {
            log.warn("Google ID token validation failed: {}", exception.getMessage());
            throw new AppException(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
        }
    }

    private JwtDecoder getJwtDecoder() {
        if (jwtDecoder == null) {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(ISSUER).build();
            OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                    JwtValidators.createDefaultWithIssuer(ISSUER),
                    this::validateAudience
            );
            decoder.setJwtValidator(validator);
            jwtDecoder = decoder;
        }

        return jwtDecoder;
    }

    private OAuth2TokenValidatorResult validateAudience(Jwt jwt) {
        if (StringUtils.hasText(clientId) && jwt.getAudience().contains(clientId)) {
            return OAuth2TokenValidatorResult.success();
        }

        log.warn("Google ID token audience mismatch. expected={}, actual={}", clientId, jwt.getAudience());

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "Google ID token audience does not match google.oauth.client-id",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
