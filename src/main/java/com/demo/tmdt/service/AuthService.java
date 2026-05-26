package com.demo.tmdt.service;

import com.demo.tmdt.common.exception.InvalidRefreshTokenException;
import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import com.demo.tmdt.dto.response.AuthResponse;
import com.demo.tmdt.enums.Role;
import com.demo.tmdt.model.Session;
import com.demo.tmdt.model.User;
import com.demo.tmdt.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)


public class AuthService {

    UserRepository userRepository;
    SessionService sessionService;
    JWTService jwtService;
    PasswordEncoder passwordEncoder;
    GoogleOidcService googleOidcService;

    public User register(String email, String password, String name , String sdt) {

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .ten(name)
                .matKhauHash(passwordEncoder.encode(password))
                .soDienThoai(sdt)
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public AuthResponse login(String email, String password, String deviceInfo) {
        return login(email, password, false, deviceInfo);
    }

    public AuthResponse login(String email, String password, boolean rememberMe, String deviceInfo) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!passwordEncoder.matches(password, user.getMatKhauHash())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        return issueTokens(user, rememberMe, deviceInfo);
    }

    public AuthResponse loginWithGoogle(String idToken, boolean rememberMe, String deviceInfo) {
        GoogleIdentity identity = googleOidcService.verify(idToken);

        User user = userRepository.findByEmail(identity.email())
                .orElseGet(() -> createGoogleUser(identity));

        return issueTokens(user, rememberMe, deviceInfo);
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public AuthResponse refresh(String refreshToken) {
        SignedJWT jwt = verifyRefreshJwt(refreshToken);

        String userId = getRequiredUserId(jwt);
        String sessionId = getRequiredSessionId(jwt);

        Session session = sessionService.findById(sessionId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (session.isRevoked() || sessionService.isExpired(session)) {
            throw new InvalidRefreshTokenException();
        }

        if (!sessionService.matchesRefreshToken(session, refreshToken)) {
            sessionService.revoke(sessionId);
            throw new InvalidRefreshTokenException();
        }

        if (session.getUser() == null || !userId.equals(session.getUser().getId())) {
            sessionService.revoke(sessionId);
            throw new InvalidRefreshTokenException();
        }

        String newAccessToken = jwtService.generateAccessToken(userId, sessionId);
        String newRefreshToken = jwtService.generateRefreshToken(userId, sessionId, session.isRememberMe());
        sessionService.saveTokenHash(session, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken, session.isRememberMe());
    }

    public User getMe(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private SignedJWT verifyRefreshJwt(String refreshToken) {
        try {
            SignedJWT jwt = jwtService.verifyToken(refreshToken);
            if (!"refresh".equals(jwtService.getType(jwt))) {
                throw new InvalidRefreshTokenException();
            }
            return jwt;
        } catch (InvalidRefreshTokenException e) {
            throw e;
        } catch (RuntimeException | ParseException e) {
            throw new InvalidRefreshTokenException();
        }
    }

    private String getRequiredUserId(SignedJWT jwt) {
        try {
            String userId = jwtService.getUserId(jwt);
            if (!StringUtils.hasText(userId)) {
                throw new InvalidRefreshTokenException();
            }
            return userId;
        } catch (InvalidRefreshTokenException e) {
            throw e;
        } catch (ParseException e) {
            throw new InvalidRefreshTokenException();
        }
    }

    private String getRequiredSessionId(SignedJWT jwt) {
        try {
            String sessionId = jwtService.getSessionId(jwt);
            if (!StringUtils.hasText(sessionId)) {
                throw new InvalidRefreshTokenException();
            }
            return sessionId;
        } catch (InvalidRefreshTokenException e) {
            throw e;
        } catch (ParseException e) {
            throw new InvalidRefreshTokenException();
        }
    }

    private AuthResponse issueTokens(User user, boolean rememberMe, String deviceInfo) {
        Session session = sessionService.createSession(user, rememberMe, deviceInfo);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), session.getId(), rememberMe);
        sessionService.saveTokenHash(session.getId(), refreshToken);
        String accessToken = jwtService.generateAccessToken(user.getId(), session.getId());

        return new AuthResponse(accessToken, refreshToken, rememberMe);
    }

    private User createGoogleUser(GoogleIdentity identity) {
        String name = StringUtils.hasText(identity.name()) ? identity.name() : identity.email();
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(identity.email())
                .ten(name)
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }
}
