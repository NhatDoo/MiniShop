package com.demo.tmdt.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private static final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 15; // 15m
    private static final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24; // 1 day
    private static final long REMEMBER_ME_REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 30; // 30 days
    private static final JWSAlgorithm TOKEN_ALGORITHM = JWSAlgorithm.HS512;

    @PostConstruct
    void validateSecretKey() {
        if (SECRET_KEY.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalStateException("jwt.secret must be at least 64 bytes for HS512");
        }
    }

    public String generateAccessToken(String userId, String sessionId) {
        return generateToken(userId, sessionId, "access", ACCESS_TOKEN_EXPIRE);
    }

    public String generateRefreshToken(String userId, String sessionId, boolean rememberMe) {
        long expireMs = rememberMe ? REMEMBER_ME_REFRESH_TOKEN_EXPIRE : REFRESH_TOKEN_EXPIRE;
        return generateToken(userId, sessionId, "refresh", expireMs);
    }

    private String generateToken(String userId, String sessionId, String type, long expireMs) {
        try {
            JWSHeader header = new JWSHeader(TOKEN_ALGORITHM);
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .jwtID(UUID.randomUUID().toString())
                    .claim("sid", sessionId)
                    .claim("type", type)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expireMs))
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Cannot generate JWT", e);
        }
    }

    public SignedJWT verifyToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!TOKEN_ALGORITHM.equals(jwt.getHeader().getAlgorithm())) {
                throw new RuntimeException("Invalid JWT algorithm");
            }

            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            if (!jwt.verify(verifier)) {
                throw new RuntimeException("Invalid signature");
            }

            JWTClaimsSet claims;

            try {
                claims = jwt.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new RuntimeException("Invalid JWT claims format", e);
            }

            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())) {
                throw new RuntimeException("Token expired");
            }

            return jwt;

        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT format", e);
        } catch (JOSEException e) {
            throw new RuntimeException("JWT verification error", e);
        }
    }

    public String getUserId(SignedJWT jwt) throws ParseException {
        return jwt.getJWTClaimsSet().getSubject();
    }

    public String getSessionId(SignedJWT jwt) throws ParseException {
        return (String) jwt.getJWTClaimsSet().getClaim("sid");
    }

    public String getType(SignedJWT jwt) throws ParseException {
        return (String) jwt.getJWTClaimsSet().getClaim("type");
    }
}
