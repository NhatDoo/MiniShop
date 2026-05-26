package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.CurrentUser;
import com.demo.tmdt.common.exception.InvalidRefreshTokenException;
import com.demo.tmdt.common.security.RefreshTokenCookie;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.dto.request.GoogleLoginRequest;
import com.demo.tmdt.dto.request.LoginRequest;
import com.demo.tmdt.dto.request.RegisterRequest;
import com.demo.tmdt.dto.response.AuthResponse;
import com.demo.tmdt.dto.response.LoginResponse;
import com.demo.tmdt.dto.response.UserResponse;
import com.demo.tmdt.mapper.UserMapper;
import com.demo.tmdt.model.User;
import com.demo.tmdt.service.AuthService;
import com.demo.tmdt.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
                request.email(),
                request.password(),
                request.name(),
                request.sdt()
        );

        return userMapper.toResponse(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT tokens")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse result = authService.login(
                request.email(),
                request.password(),
                request.shouldRemember(),
                request.deviceInfo()
        );

        response.addHeader(HttpHeaders.SET_COOKIE, RefreshTokenCookie.create(result.refreshToken(), result.rememberMe()).toString());

        return new LoginResponse(result.accessToken());
    }

    @PostMapping("/google")
    @Operation(summary = "Login with Google OIDC ID token")
    public LoginResponse loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse result = authService.loginWithGoogle(
                request.idToken(),
                request.shouldRemember(),
                request.deviceInfo()
        );

        response.addHeader(HttpHeaders.SET_COOKIE, RefreshTokenCookie.create(result.refreshToken(), result.rememberMe()).toString());

        return new LoginResponse(result.accessToken());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        String refreshToken = RefreshTokenCookie.extract(request).orElseThrow(InvalidRefreshTokenException::new);
        AuthResponse result = authService.refresh(refreshToken);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, RefreshTokenCookie.create(result.refreshToken(), result.rememberMe()).toString())
                .body(new LoginResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current session", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {
        sessionService.revoke(currentUser.getSessionId());
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, RefreshTokenCookie.clear().toString())
                .build();
    }
}
