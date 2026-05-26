package com.demo.tmdt.controller;

import com.demo.tmdt.common.annotation.CurrentUser;
import com.demo.tmdt.common.security.UserPrincipal;
import com.demo.tmdt.dto.response.UserResponse;
import com.demo.tmdt.mapper.UserMapper;
import com.demo.tmdt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current user", security = @SecurityRequirement(name = "bearerAuth"))
    public UserResponse getMe(@Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {
        return userMapper.toResponse(authService.getMe(currentUser.getUserId()));
    }
}
