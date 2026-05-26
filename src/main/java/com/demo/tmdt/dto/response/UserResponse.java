package com.demo.tmdt.dto.response;

import com.demo.tmdt.enums.Role;

public record UserResponse(
        String id,
        String email,
        String name,
        String phone,
        Role role
) {}
